package org.entando.entando.aps.system.services.imageresize;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import org.apache.commons.io.FilenameUtils;
import org.entando.entando.aps.system.services.storage.IStorageManager;
import org.entando.entando.aps.system.services.userprofilepicture.UserProfilePicture;
import org.entando.entando.aps.system.services.userprofilepicture.UserProfilePictureFile;
import org.entando.entando.aps.system.services.userprofilepicture.UserProfilePictureVersion;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.exception.EntRuntimeException;
import org.springframework.util.Assert;

public class ImageResizeService {

    private IStorageManager storageManager;
    private PNGImageResizer pngImageResizer;
    private DefaultImageResizer defaultImageResizer;
    private final List<ImageResizeDimension> dimensions = createDimensions();

    public File saveTempFile(String filename, InputStream is) throws EntException, IOException {
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
        } catch (Exception e) {
            throw new EntException("Error on saving temporary file", e);
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

    private void saveResizedImage(UserProfilePictureFile file, ImageIcon imageIcon, ImageResizeDimension dimension,
            UserProfilePictureVersion version, String path) throws EntException {
        if (dimension.getIdDim() == 0) {
            return;
        }

        String imageName = getNewInstanceFileName(file.getFilename(), dimension.getIdDim(), null);
        String filePath = path + imageName;

        try {
            version.setDimensions(String.format("%dx%d px", dimension.getDimx(), dimension.getDimy()));
            version.setPath(filePath);
            if(file.getMimeType().contains("svg")) {
                long realLength = calculateSize(file.getFile().length());
                version.setSize(realLength + " Kb");
                this.storageManager.saveFile(filePath, false, new FileInputStream(file.getFile()));
            }else {
                storageManager.deleteFile(filePath, false);
                IImageResizer resizer = this.getImageResizer(filePath);
                resizer.saveResizedImage(filePath, false, imageIcon, dimension, version);
            }
        } catch (Exception e) {
            throw new EntException("Error creating resource file instance '" + filePath + "'", e);
        }
    }

    public void saveResizedImages(UserProfilePicture result, String masterFilePath, UserProfilePictureFile file,
            String path)
            throws EntException {
        try {
            for (ImageResizeDimension dimension : dimensions) {
                UserProfilePictureVersion version = new UserProfilePictureVersion();
                version.setUsername(result.getUsername());
                result.getVersions().add(version);
                ImageIcon imageIcon = new ImageIcon(masterFilePath);
                this.saveResizedImage(file, imageIcon, dimension, version, path);
            }
        } catch (Exception e) {
            throw new EntException("Error saving resized image resource instances", e);
        }
    }

    public String getNewInstanceFileName(String masterFileName, int size, String langCode) {
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

    protected boolean exists(String filePath) {
        try {
            return storageManager.exists(filePath, false);
        } catch (Exception e) {
            throw new EntRuntimeException("Error testing existing file " + filePath, e);
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

    private int calculateSize(long length) {
        return (int)Math.ceil(length / 1000.0);
    }

    private List<ImageResizeDimension> createDimensions() {
        List<ImageResizeDimension> result = new ArrayList<>();

        result.add(new ImageResizeDimension(1, 90, 90));
        result.add(new ImageResizeDimension(2, 130, 130));
        result.add(new ImageResizeDimension(3, 150, 150));

        return result;
    }

    public void setStorageManager(IStorageManager storageManager) {
        this.storageManager = storageManager;
    }

    public void setPngImageResizer(PNGImageResizer pngImageResizer) {
        this.pngImageResizer = pngImageResizer;
    }

    public void setDefaultImageResizer(
            DefaultImageResizer defaultImageResizer) {
        this.defaultImageResizer = defaultImageResizer;
    }
}
