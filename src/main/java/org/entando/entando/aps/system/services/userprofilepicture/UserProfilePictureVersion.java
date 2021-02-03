package org.entando.entando.aps.system.services.userprofilepicture;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "userProfilePictureVersion")
@XmlType(propOrder = {"username, dimensions", "path", "size"})
public class UserProfilePictureVersion {

    private String username;
    private String dimensions;
    private String path;
    private String size;

    @XmlElement(name = "username", required = true)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @XmlElement(name = "dimensions")
    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    @XmlElement(name = "path", required = true)
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @XmlElement(name = "size", required = true)
    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "ProfilePictureVersion{" +
                "dimensions='" + dimensions + '\'' +
                ", path='" + path + '\'' +
                ", size='" + size + '\'' +
                '}';
    }
}
