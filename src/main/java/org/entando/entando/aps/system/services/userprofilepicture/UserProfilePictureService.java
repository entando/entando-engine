/*
 * Copyright 2018-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.services.userprofilepicture;

import com.agiletec.aps.system.services.user.UserDetails;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.swing.ImageIcon;
import org.apache.commons.io.FilenameUtils;
import org.entando.entando.aps.system.exception.RestServerError;
import org.entando.entando.aps.system.services.image.DefaultImageResizer;
import org.entando.entando.aps.system.services.image.IImageResizer;
import org.entando.entando.aps.system.services.image.ImageDimension;
import org.entando.entando.aps.system.services.image.PNGImageResizer;
import org.entando.entando.aps.system.services.storage.IStorageManager;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.web.userprofilepicture.model.UserProfilePictureDto;
import org.entando.entando.web.userprofilepicture.model.UserProfilePictureVersionDto;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

public class UserProfilePictureService implements IUserProfilePictureService {

    private IUserProfilePictureManager userProfilePictureManager;
    private IStorageManager storageManager;

    private String folder = "testingfolders";
    private List<ImageDimension> dimensions = createDimensions();
    private PNGImageResizer pngImageResizer = new PNGImageResizer();
    private DefaultImageResizer defaultImageResizer = new DefaultImageResizer();

    @PostConstruct
    public void setup() {
        pngImageResizer.setStorageManager(storageManager);
        defaultImageResizer.setStorageManager(storageManager);
    }


    @Override
    public UserProfilePictureDto getUserProfilePicture(UserDetails user) {
        try {
            UserProfilePicture userProfilePicture = userProfilePictureManager.getUserProfilePicture(user.getUsername());
            return userProfilePictureToDto(userProfilePicture);
        } catch (EntException e) {
            throw new RestServerError("Error getting user profile picture", e);
        }
    }

    @Override
    public UserProfilePictureDto addUserProfilePicture(MultipartFile file, UserDetails user) {

        try {
            UserProfilePicture userProfilePicture = createUserProfilePicture(file, user);
            userProfilePictureManager.addUserProfilePicture(userProfilePicture);
            return getUserProfilePicture(user);
        } catch (EntException e) {
            throw new RestServerError("Error reading file input stream", e);
        }
    }

    @Override
    public UserProfilePictureDto updateUserProfilePicture(MultipartFile file, UserDetails user) {
        try {
            UserProfilePicture userProfilePicture = createUserProfilePicture(file, user);
            userProfilePictureManager.updateUserProfilePicture(userProfilePicture);
            deleteUserProfilePictureFiles(user);
            return getUserProfilePicture(user);
        } catch (EntException e) {
            throw new RestServerError("Error updating user profile picture", e);
        }
    }

    @Override
    public void deleteUserProfilePicture(UserDetails user) {
        try {
            userProfilePictureManager.deleteUserProfilePicture(user.getUsername());
            deleteUserProfilePictureFiles(user);
        } catch (EntException e) {
            throw new RestServerError("Error updating user profile picture", e);
        }
    }

    private UserProfilePicture createUserProfilePicture(MultipartFile multipartFile, UserDetails user) {
        UserProfilePicture result = new UserProfilePicture();
        result.setUsername(user.getUsername());

        try {
            UserProfilePictureFile file = new UserProfilePictureFile();

            file.setInputStream(multipartFile.getInputStream());
            file.setFileSize(calculateSize(multipartFile.getBytes().length));
            file.setFileName(multipartFile.getOriginalFilename());
            file.setMimeType(multipartFile.getContentType());
            file.setUser(user);

            String masterImageFileName = getNewInstanceFileName(file.getFileName(), 0, null);
            String subPath = folder + masterImageFileName;
            storageManager.deleteFile(subPath, false);
            File tempMasterFile = this.saveTempFile(masterImageFileName, file.getInputStream());

            file.setFile(tempMasterFile);

            UserProfilePictureVersion version = new UserProfilePictureVersion();
            version.setUsername(result.getUsername());
            version.setPath(subPath);
            version.setSize(file.getFileSize() + " Kb");
            result.getVersions().add(version);

            this.saveResizedInstances(result, subPath, file);
            this.storageManager.saveFile(subPath,false, new FileInputStream(tempMasterFile));

            tempMasterFile.delete();
        } catch (IOException | EntException e) {
            //TODO e.printStackTrace();
        }

        /*UserProfilePicture result = new UserProfilePicture();
        result.setUsername(user.getUsername());

        UserProfilePictureVersion ppv1 = new UserProfilePictureVersion();
        ppv1.setUsername(user.getUsername());
        ppv1.setPath("/entando-de-app/engine/admin/profile/image_d0.jpg");
        ppv1.setSize("2 Kb");
        result.getVersions().add(ppv1);

        UserProfilePictureVersion ppv2 = new UserProfilePictureVersion();
        ppv2.setUsername(user.getUsername());
        ppv2.setDimensions("90x90 px");
        ppv2.setPath("/entando-de-app/engine/admin/profile/image_d1.jpg");
        ppv2.setSize("2 Kb");
        result.getVersions().add(ppv2);

        UserProfilePictureVersion ppv3 = new UserProfilePictureVersion();
        ppv3.setUsername(user.getUsername());
        ppv3.setDimensions("130x130 px");
        ppv3.setPath("/entando-de-app/engine/admin/profile/image_d2.jpg");
        ppv3.setSize("2 Kb");
        result.getVersions().add(ppv3);

        UserProfilePictureVersion ppv4 = new UserProfilePictureVersion();
        ppv4.setUsername(user.getUsername());
        ppv4.setDimensions("150x150 px");
        ppv4.setPath("/entando-de-app/engine/admin/profile/image_d3.jpg");
        ppv4.setSize("2 Kb");
        result.getVersions().add(ppv4);*/

        return result;
    }

    private int calculateSize(long length) {
        return (int)Math.ceil(length / 1000.0);
    }

    String getNewInstanceFileName(String masterFileName, int size, String langCode) {
        String baseName = FilenameUtils.getBaseName(masterFileName);
        String extension = FilenameUtils.getExtension(masterFileName);
        String suffix = "";
        if (size >= 0) {
            suffix += "_d" + size;
        }
        if (langCode != null) {
            suffix += "_" + langCode;
        }
        return this.createFileName(getMultiFileUniqueBaseName(baseName, suffix, extension), extension);
    }

    protected String createFileName(String baseName, String extension) {
        return extension == null ? baseName : baseName + '.' + extension;
    }

    protected String getMultiFileUniqueBaseName(String baseName, String suffix, String extension) {
        Assert.hasLength(baseName, "base name of file can't be null or empty");
        Assert.notNull(suffix, "file suffix can't be null");
        baseName = this.purgeBaseName(baseName);
        String suggestedName = baseName + suffix;
        int fileOrder = 1;
        while(this.exists(this.createFileName(suggestedName, extension))) {
            suggestedName = baseName + '_' + fileOrder + suffix;
            fileOrder ++;
        }
        return suggestedName;
    }

    private String purgeBaseName(String baseName) {
        String purgedName = baseName.replaceAll("[^ _.a-zA-Z0-9]", "");
        return purgedName.trim().replace(' ', '_');
    }

    protected boolean exists(String instanceFileName) {
        try {
            String subPath = folder + instanceFileName;
            return storageManager.exists(subPath, false);
        } catch (Throwable t) {
            throw new RuntimeException("Error testing existing file " + instanceFileName, t);
        }
    }

    protected File saveTempFile(String filename, InputStream is) throws EntException, IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        String filePath = tempDir + File.separator + filename;
        FileOutputStream outStream = null;
        try {
            byte[] buffer = new byte[1024];
            int length;
            outStream = new FileOutputStream(filePath);
            while ((length = is.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
                outStream.flush();
            }
        } catch (Throwable t) {
            throw new EntException("Error on saving temporary file", t);
        } finally {
            if (null != outStream) {
                outStream.close();
            }
            if (null != is) {
                is.close();
            }
        }
        return new File(filePath);
    }

    private void saveResizedInstances(UserProfilePicture result, String masterFilePath, UserProfilePictureFile file)
            throws EntException {
        try {
            for (ImageDimension dimension : dimensions) {
                UserProfilePictureVersion version = new UserProfilePictureVersion();
                version.setUsername(result.getUsername());
                result.getVersions().add(version);
                ImageIcon imageIcon = new ImageIcon(masterFilePath);
                this.saveResizedImage(file, imageIcon, dimension, version);
            }
        } catch (Throwable t) {
            throw new EntException("Error saving resized image resource instances", t);
        }
    }

    private void saveResizedImage(UserProfilePictureFile file, ImageIcon imageIcon, ImageDimension dimension,
            UserProfilePictureVersion version) throws EntException {
        if (dimension.getIdDim() == 0) {
            return;
        }

        String imageName = getNewInstanceFileName(file.getFileName(), dimension.getIdDim(), null);
        String subPath = folder + imageName;

        try {
            version.setDimensions(String.format("%dx%d px", dimension.getDimx(), dimension.getDimy()));
            version.setPath(subPath);
            if(file.getMimeType().contains("svg")) {
                long realLength = calculateSize(file.getFile().length());
                version.setSize(realLength + " Kb");
                this.storageManager.saveFile(subPath, false, new FileInputStream(file.getFile()));
            }else {
                storageManager.deleteFile(subPath, false);
                IImageResizer resizer = this.getImageResizer(subPath);
                resizer.saveResizedImage(subPath, false, imageIcon, dimension, version);
            }
        } catch (Throwable t) {
            throw new EntException("Error creating resource file instance '" + subPath + "'", t);
        }
    }

    private IImageResizer getImageResizer(String filePath) {
        String extension = FilenameUtils.getExtension(filePath);
        if ("png".equals(extension)) {
            return pngImageResizer;
        } else {
            return defaultImageResizer;
        }
    }

    //TODO Implement
    private void deleteUserProfilePictureFiles(UserDetails user) {
        return;
    }

    private UserProfilePictureDto userProfilePictureToDto(UserProfilePicture userProfilePicture) {
        if (userProfilePicture != null) {
            UserProfilePictureDto result = new UserProfilePictureDto();
            result.setUsername(userProfilePicture.getUsername());

            for (UserProfilePictureVersion version : userProfilePicture.getVersions()) {
                UserProfilePictureVersionDto versionDto = new UserProfilePictureVersionDto();
                versionDto.setDimensions(version.getDimensions());
                versionDto.setPath(version.getPath());
                versionDto.setSize(version.getSize());
                result.getVersions().add(versionDto);
            }

            return result;
        }
        return null;
    }

    private List<ImageDimension> createDimensions() {
        List<ImageDimension> result = new ArrayList();

        result.add(new ImageDimension(1, 90, 90));
        result.add(new ImageDimension(2, 130, 130));
        result.add(new ImageDimension(3, 150, 150));

        return result;
    }

    public void setUserProfilePictureManager(
            IUserProfilePictureManager userProfilePictureManager) {
        this.userProfilePictureManager = userProfilePictureManager;
    }

    public void setStorageManager(IStorageManager storageManager) {
        this.storageManager = storageManager;
    }
}
