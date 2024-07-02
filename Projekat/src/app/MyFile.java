package app;

import java.io.File;
import java.io.Serializable;
import java.util.Objects;

public class MyFile implements Serializable {

    private File file;
    private String path;

    private String visibility;

    public MyFile(File file, String path, String visibility) {
        this.file = file;
        this.path = path;
        this.visibility = visibility;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    @Override
    public String toString() {
        return "MyFile{" +
                "file=" + file +
                ", path='" + path + '\'' +
                ", visibility='" + visibility + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyFile myFile = (MyFile) o;
        return Objects.equals(path, myFile.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
