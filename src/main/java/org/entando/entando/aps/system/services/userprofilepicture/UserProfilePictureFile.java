package org.entando.entando.aps.system.services.userprofilepicture;

import com.agiletec.aps.system.services.user.UserDetails;
import java.io.File;
import java.io.InputStream;

public class UserProfilePictureFile {

    private File file;
    private String mimeType;
    private String filename;
    private InputStream inputStream;
    private int fileSize;
    private UserDetails user;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public UserDetails getUser() {
        return user;
    }

    public void setUser(UserDetails user) {
        this.user = user;
    }
}
