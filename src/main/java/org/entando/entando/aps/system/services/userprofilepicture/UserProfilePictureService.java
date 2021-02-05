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
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.aps.system.exception.RestServerError;
import org.entando.entando.aps.system.services.imageresize.ImageResizeService;
import org.entando.entando.aps.system.services.storage.IStorageManager;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.web.userprofilepicture.model.UserProfilePictureDto;
import org.entando.entando.web.userprofilepicture.model.UserProfilePictureVersionDto;
import org.springframework.web.multipart.MultipartFile;

public class UserProfilePictureService implements IUserProfilePictureService {

    private static final EntLogger logger = EntLogFactory.getSanitizedLogger(UserProfilePictureService.class);

    private IUserProfilePictureManager userProfilePictureManager;
    private IStorageManager storageManager;
    private ImageResizeService imageResizeService;

    private String folder = "profile";

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
            deleteFiles(user);
            UserProfilePicture userProfilePicture = createUserProfilePicture(file, user);
            userProfilePictureManager.updateUserProfilePicture(userProfilePicture);
            return getUserProfilePicture(user);
        } catch (EntException e) {
            throw new RestServerError("Error updating user profile picture", e);
        }
    }

    @Override
    public void deleteUserProfilePicture(UserDetails user) {
        try {
            deleteFiles(user);
            userProfilePictureManager.deleteUserProfilePicture(user.getUsername());
        } catch (EntException e) {
            throw new RestServerError("Error updating user profile picture", e);
        }
    }

    private UserProfilePicture createUserProfilePicture(MultipartFile multipartFile, UserDetails user) {
        UserProfilePicture result = new UserProfilePicture();
        String username = user.getUsername();
        result.setUsername(username);

        try {
            UserProfilePictureFile file = createUserProfilePictureFile(multipartFile, user);

            String fullPath =
                    getVersionPath(username) + getUniqueFilename(getVersionPath(username) + file.getFilename());

            UserProfilePictureVersion version = new UserProfilePictureVersion();
            version.setUsername(username);
            version.setPath(fullPath);
            version.setSize((int)Math.ceil(file.getFileSize() / 1000.0) + " Kb");
            result.getVersions().add(version);

            storageManager.saveFile(fullPath,false, new FileInputStream(file.getFile()));
            imageResizeService.saveResizedImages(result, fullPath, file, getVersionPath(username));

            boolean tempFileDeleted = file.getFile().delete();
            if (!tempFileDeleted) {
                logger.warn("Failed to delete temp file {}", file.getFilename());
            }
        } catch (IOException | EntException e) {
            //TODO treat this properly
            e.printStackTrace();
        }

        return result;
    }

    private File saveTempFile(UserProfilePictureFile file, String notResizedFilename) throws EntException, IOException {
        return imageResizeService.saveTempFile(notResizedFilename, file.getInputStream());
    }

    private String getUniqueFilename(String filename) {
        return imageResizeService.getNewInstanceFileName(filename, 0, null);
    }

    private UserProfilePictureFile createUserProfilePictureFile(MultipartFile multipartFile, UserDetails user)
            throws IOException, EntException {
        UserProfilePictureFile file = new UserProfilePictureFile();

        file.setInputStream(multipartFile.getInputStream());
        file.setFileSize(multipartFile.getBytes().length);
        file.setFilename(multipartFile.getOriginalFilename());
        file.setMimeType(multipartFile.getContentType());
        file.setUser(user);
        file.setFile(saveTempFile(file, multipartFile.getOriginalFilename()));

        return file;
    }

    private String getVersionPath(String username) {
        return StringUtils.join(storageManager.getResourceUrl(folder, false), "/", username, "/");
    }

    private void deleteFiles(UserDetails user) throws EntException {
        UserProfilePicture userProfilePicture = userProfilePictureManager.getUserProfilePicture(user.getUsername());
        for (UserProfilePictureVersion version : userProfilePicture.getVersions()) {
            storageManager.deleteFile(version.getPath(), false);
        }
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

    public void setImageResizeService(ImageResizeService imageResizeService) {
        this.imageResizeService = imageResizeService;
    }

    public void setUserProfilePictureManager(
            IUserProfilePictureManager userProfilePictureManager) {
        this.userProfilePictureManager = userProfilePictureManager;
    }

    public void setStorageManager(IStorageManager storageManager) {
        this.storageManager = storageManager;
    }
}
