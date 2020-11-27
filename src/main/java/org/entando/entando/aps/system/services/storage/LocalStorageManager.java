/*
 * Copyright 2015-Present Entando S.r.l. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package org.entando.entando.aps.system.services.storage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.CharEncoding;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.exception.EntRuntimeException;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.springframework.lang.Nullable;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;

public class LocalStorageManager implements IStorageManager {

	private static final EntLogger logger = EntLogFactory.getSanitizedLogger(LocalStorageManager.class);

	private String baseURL;
	private String baseDiskRoot;
	private String protectedBaseDiskRoot;
	private String protectedBaseURL;
	private String allowedEditExtensions;


	public void init() throws Exception {
		logger.debug("{} ready", this.getClass().getName());
	}

	@Override
	public void saveFile(String subPath, boolean isProtectedResource, InputStream is) throws EntException, IOException {
		subPath = (null == subPath) ? "" : subPath;
		String fullPath = this.createFullPath(subPath, isProtectedResource);
		FileOutputStream outStream = null;
		try {
			File dir = new File(fullPath).getParentFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}
			byte[] buffer = new byte[1024];
			int length;
			outStream = new FileOutputStream(fullPath);
			while ((length = is.read(buffer)) != -1) {
				outStream.write(buffer, 0, length);
				outStream.flush();
			}
		} catch (Throwable t) {
			logger.error("Error on saving file", t);
			throw new EntException("Error on saving file", t);
		} finally {
			if (null != outStream) {
				outStream.close();
			}
			if (null != is) {
				is.close();
			}
		}
	}

	@Override
	public boolean deleteFile(String subPath, boolean isProtectedResource) {
		subPath = (null == subPath) ? "" : subPath;
		String diskRoot = (!isProtectedResource) ? this.getBaseDiskRoot() : this.getProtectedBaseDiskRoot();
		String fullPath = this.createFullPath(subPath, isProtectedResource);
		try {
			File fileUnsafe = new File(fullPath);
			File directory = new File(diskRoot);
			if (isSamePath(diskRoot, fullPath) || FileUtils.directoryContains(directory, fileUnsafe)) {
				if (fileUnsafe.exists()) {
					return fileUnsafe.delete();
				}
			} else {
				throw new IOException(
						String.format("Path validation failed: \"%s\" not in \"%s\"", diskRoot, subPath)
				);
			}
		} catch (IOException e) {
			logger.error("Error while deleting file", e);
		}
		return false;
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	@Override
	public void createDirectory(String subPath, boolean isProtectedResource) {
		subPath = (null == subPath) ? "" : subPath;
		String fullPath = this.createFullPath(subPath, isProtectedResource);
		String diskRoot = (!isProtectedResource) ? this.getBaseDiskRoot() : this.getProtectedBaseDiskRoot();
		try {
			if (isSubPathOf(diskRoot, fullPath)) {
				File dir = new File(fullPath);
				if (!dir.exists() || !dir.isDirectory()) {
					dir.mkdirs();
				}
			} else {
				throw new IOException("Path traversal detected");
			}
		} catch (IOException e) {
			throw new EntRuntimeException("Error validating the path", e);
		}
	}

	public void deleteDirectory(String subPath, boolean isProtectedResource) throws EntException {
		safeDeleteDirectory(null, subPath, isProtectedResource);
	}

	@Override
	public void safeDeleteDirectory(@Nullable String basePath, String subPath, boolean isProtectedResource) throws EntException {
		if (basePath == null)
			basePath = (!isProtectedResource) ? this.getBaseDiskRoot() : this.getProtectedBaseDiskRoot();

		subPath = (null == subPath) ? "" : subPath;
		String fullPath = this.createFullPath(subPath, isProtectedResource);
		try {
			File targetDir = new File(fullPath);
			File baseDir = new File(basePath);
			if (isSamePath(basePath, fullPath) || FileUtils.directoryContains(baseDir, targetDir)) {
				this.delete(targetDir);
			} else {
				throw new IOException(
						String.format("Path validation failed: \"%s\" not in \"%s\"", subPath, basePath)
				);
			}
		} catch (IOException e) {
			throw new EntRuntimeException("Error while deleting the directory", e);
		}
	}

	private boolean delete(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				String[] filesName = file.list();
				if (filesName != null) {
					for (String s : filesName) {
						File fileToDelete = new File(file.getAbsoluteFile() + File.separator + s);
						this.delete(fileToDelete);
					}
				}
				boolean deleted = file.delete();

				if(!deleted) {
					logger.warn("Failed to delete  file {}", file.getAbsolutePath());
				}
			} else {
				return file.delete();
			}
		}
		return false;
	}

	@Override
	public InputStream getStream(String subPath, boolean isProtectedResource) throws EntException {
		try {
			subPath = (null == subPath) ? "" : subPath;
			String fullPath = this.createFullPath(subPath, isProtectedResource);
			String diskRoot = (!isProtectedResource) ? this.getBaseDiskRoot() : this.getProtectedBaseDiskRoot();
			File file = new File(fullPath);
			if (isSamePath(diskRoot, fullPath) || FileUtils.directoryContains(new File(diskRoot), file)) {
				if (file.exists() && !file.isDirectory()) {
					return new FileInputStream(file);
				}
			}
		} catch (Throwable t) {
			logger.error("Error extracting stream", t);
			throw new EntException("Error extracting stream", t);
		}
		return null;
	}

	@Override
	public boolean exists(String subPath, boolean isProtectedResource) {
		File file = this.getFile(subPath, isProtectedResource);
		return file.exists();
	}

	protected File getFile(String subPath, boolean isProtectedResource) {
		subPath = (null == subPath) ? "" : subPath;
		String fullPath = this.createFullPath(subPath, isProtectedResource);
		return new File(fullPath);
	}

	@Override
	public String getResourceUrl(String subPath, boolean isProtectedResource) {
		subPath = (null == subPath) ? "" : subPath;
		String baseUrl = (!isProtectedResource) ? this.getBaseURL() : this.getProtectedBaseURL();
		return this.createPath(baseUrl, subPath, true);
	}

	@Override
	public String readFile(String subPath, boolean isProtectedResource) throws EntException {
		subPath = (null == subPath) ? "" : subPath;
		String fullPath = this.createFullPath(subPath, isProtectedResource);
		File file = new File(fullPath);
		try {
			return FileUtils.readFileToString(file, CharEncoding.UTF_8);
		} catch (Throwable t) {
			logger.error("Error reading File with path {}", subPath, t);
			throw new EntException("Error reading file", t);
		}
	}

	@Override
	public void editFile(String subPath, boolean isProtectedResource, InputStream is) throws EntException {
		subPath = (null == subPath) ? "" : subPath;
		String fullPath = this.createFullPath(subPath, isProtectedResource);
		String tempFilePath = null;
		try {
			File oldFile = new File(fullPath);
			if (oldFile.exists()) {
				String tempDir = System.getProperty("java.io.tmpdir");
				tempFilePath = tempDir + File.separator + subPath;
				FileUtils.copyFile(oldFile, new File(tempFilePath));
			}
			this.saveFile(subPath, isProtectedResource, is);
		} catch (Throwable t) {
			try {
				if (null != tempFilePath) {
					FileUtils.moveFile(new File(tempFilePath), new File(fullPath));
				}
			} catch (Throwable tr) {
				logger.error("Error restoring File from path {} to path", tempFilePath, fullPath, tr);
			}
			logger.error("Error writing File with path {}", subPath, t);
			throw new EntException("Error editing file", t);
		} finally {
			if (null != tempFilePath) {
				boolean deleted = new File(tempFilePath).delete();

				if(!deleted) {
					logger.warn("Failed to delete  file {}", tempFilePath);
				}
			}
		}
	}

	@Override
	public String[] list(String subPath, boolean isProtectedResource) {
		return this.list(subPath, isProtectedResource, null);
	}

	@Override
	public String[] listDirectory(String subPath, boolean isProtectedResource) {
		return this.list(subPath, isProtectedResource, true);
	}

	@Override
	public String[] listFile(String subPath, boolean isProtectedResource) {
		return this.list(subPath, isProtectedResource, false);
	}

	private String[] list(String subPath, boolean isProtectedResource, Boolean searchDirectory) {
		subPath = (null == subPath) ? "" : subPath;
		String fullPath = this.createFullPath(subPath, isProtectedResource);
		File directory = new File(fullPath);
		if (directory.exists() && directory.isDirectory()) {
			String[] objects = new String[]{};
			String folder = fullPath;
			if (!folder.endsWith("/")) {
				folder += "/";
			}
			String[] contents = directory.list();
			if (null == searchDirectory) {
				objects = contents;
			} else {
				if (contents != null) {
					for (String string : contents) {
						File file = new File(folder + string);
						if ((file.isDirectory() && searchDirectory) || (!file.isDirectory() && !searchDirectory)) {
							objects = this.addChild(string, objects);
						}
					}
				}
			}
			Arrays.sort(Objects.requireNonNull(objects));
			return objects;
		}
		return null;
	}

	protected String[] addChild(String stringToAdd, String[] objects) {
		int len = objects.length;
		String[] newArray = new String[len + 1];
		System.arraycopy(objects, 0, newArray, 0, len);
		newArray[len] = stringToAdd;
		return newArray;
	}

	@Override
	public String createFullPath(String subPath, boolean isProtectedResource) {
		subPath = (null == subPath) ? "" : subPath;
		String diskRoot = (!isProtectedResource) ? this.getBaseDiskRoot() : this.getProtectedBaseDiskRoot();
		String resPath = this.createPath(diskRoot, subPath, false);
		try {
			if (!isSubPathOf(diskRoot, resPath, true)) {
				throw new IOException(
						String.format("Path traversal detected: \"%s\" not in \"%s\"", resPath, diskRoot)
				);
			}
		} catch (IOException e) {
			throw new EntRuntimeException("Error validating the path", e);
		}
		return resPath;
	}

	private String createPath(String basePath, String subPath, boolean isUrlPath) {
		subPath = (null == subPath) ? "" : subPath;
		String separator = (isUrlPath) ? "/" : File.separator;
		boolean baseEndWithSlash = basePath.endsWith(separator);
		boolean subPathStartWithSlash = subPath.startsWith(separator);
		if ((baseEndWithSlash && !subPathStartWithSlash) || (!baseEndWithSlash && subPathStartWithSlash)) {
			return basePath + subPath;
		} else if (!baseEndWithSlash && !subPathStartWithSlash) {
			return basePath + separator + subPath;
		} else {
			String base = basePath.substring(0, basePath.length() - File.separator.length());
			return base + subPath;
		}
	}

	@Override
	public BasicFileAttributeView[] listAttributes(String subPath, boolean isProtectedResource) {
		return this.listAttributes(subPath, isProtectedResource, null);
	}

	@Override
	public BasicFileAttributeView[] listDirectoryAttributes(String subPath, boolean isProtectedResource) {
		return this.listAttributes(subPath, isProtectedResource, true);
	}

	@Override
	public BasicFileAttributeView[] listFileAttributes(String subPath, boolean isProtectedResource) {
		return this.listAttributes(subPath, isProtectedResource, false);
	}

	private BasicFileAttributeView[] listAttributes(String subPath, boolean isProtectedResource, Boolean searchDirectory) {
		subPath = (null == subPath) ? "" : subPath;
		String fullPath = this.createFullPath(subPath, isProtectedResource);
		String diskRoot = (!isProtectedResource) ? this.getBaseDiskRoot() : this.getProtectedBaseDiskRoot();

		try {
			File directory = new File(fullPath);
			if (isSamePath(diskRoot, fullPath) || FileUtils.directoryContains(new File(diskRoot), directory)) {
				if (directory.exists() && directory.isDirectory()) {
					BasicFileAttributeView[] objects = new BasicFileAttributeView[]{};
					String folder = fullPath;
					if (!folder.endsWith("/")) {
						folder += "/";
					}
					String[] contents = directory.list();
					for (String string : Objects.requireNonNull(contents)) {
						File file = new File(folder + string);
						if (null == searchDirectory || (file.isDirectory() && searchDirectory) || (!file.isDirectory() && !searchDirectory)) {
							BasicFileAttributeView bfav = new BasicFileAttributeView(file);
							objects = this.addChild(bfav, objects);
						}
					}
					Arrays.sort(objects);
					return objects;
				}
			}
		} catch (IOException ignore) {
		}
		return null;
	}

	protected BasicFileAttributeView[] addChild(BasicFileAttributeView elementToAdd, BasicFileAttributeView[] objects) {
		int len = objects.length;
		BasicFileAttributeView[] newArray = new BasicFileAttributeView[len + 1];
		System.arraycopy(objects, 0, newArray, 0, len);
		newArray[len] = elementToAdd;
		return newArray;
	}

	@Override
	public BasicFileAttributeView getAttributes(String subPath, boolean isProtectedResource) {
		File file = this.getFile(subPath, isProtectedResource);
		if (!file.exists()) {
			return null;
		} else {
			return new BasicFileAttributeView(file);
		}
	}

	protected String getBaseURL() {
		return baseURL;
	}

	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	protected String getBaseDiskRoot() {
		return baseDiskRoot;
	}

	public void setBaseDiskRoot(String baseDiskRoot) {
		this.baseDiskRoot = baseDiskRoot;
	}

	protected String getProtectedBaseDiskRoot() {
		return protectedBaseDiskRoot;
	}

	public void setProtectedBaseDiskRoot(String protBaseDiskRoot) {
		this.protectedBaseDiskRoot = protBaseDiskRoot;
	}

	protected String getProtectedBaseURL() {
		return protectedBaseURL;
	}

	public void setProtectedBaseURL(String protBaseURL) {
		this.protectedBaseURL = protBaseURL;
	}

	public void setAllowedEditExtensions(String allowedEditExtensions) {
		this.allowedEditExtensions = allowedEditExtensions;
	}

	public String getAllowedEditExtensions() {
		return allowedEditExtensions;
	}

	public static boolean isSubPathOf(String basePath, String pathToCheck) throws IOException {
		return isSubPathOf(basePath, pathToCheck, false);
	}

	public static boolean isSubPathOf(String basePath, String pathToCheck, boolean baseIncludesBase) throws
			IOException {
		basePath = FilenameUtils.normalize(basePath);
		if (!basePath.endsWith("/")) basePath = basePath.concat("/");
		pathToCheck = FilenameUtils.normalize(pathToCheck);
		if (FilenameUtils.directoryContains(basePath, pathToCheck)) {
			return true;
		} else {
			if (baseIncludesBase && basePath.equals(pathToCheck)) {
				return true;
			} else {
				return false;
			}
		}
	}

	public static boolean isSamePath(String path1, String path2) {
		if (!path1.endsWith("/")) path1 = path1.concat("/");
		if (!path2.endsWith("/")) path2 = path2.concat("/");
		return path1.equals(path2);
	}
}
