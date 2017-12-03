package com.example.otaviorodriguesdeoli.bluehack3;

/**
 * Created by OtavioRodriguesdeOli on 02-Dec-17.
 */
import java.io.*;
import com.jscape.inet.scp.Scp;
import com.jscape.inet.scp.events.*;
import com.jscape.inet.ssh.util.SshParameters;

public class SCP implements ScpEventListener {

    public String getHostname() {
        return hostname;
    }

    public String getUpDestination() {
        return upDestination;
    }

    private String hostname = "54.149.66.124";
    private String username = "ubuntu";
    private String password = "senha";
    private String upDestination = "/var/www/html/";
    private String downDestination = "/";
    // This SshParameters instance is the same for upload or download.
    private SshParameters params = new SshParameters(hostname, username, password);

    public void doUpload(String upFile) throws Exception {
        // create new Scp instance
        Scp scp = new Scp(params);
        // register event listener
        scp.addListener(this);
        // establish connection (and disconnect, below)
        scp.connect();
        // UPLOAD. The first argument is a File, the second is a String.
        scp.upload(new File(upFile), upDestination);
        scp.disconnect();
    }

    public void doDownload(String downFile) throws Exception {
        // create new Scp instance
        Scp scp = new Scp(params);
        // register event listener
        scp.addListener(this);
        // establish connection (and disconnect, below)
        scp.connect();
        // DOWNLOAD. Both arguments are Strings.
        scp.download(downDestination, downFile);
        scp.disconnect();
    }

    // Various status messages.
    public void download(ScpFileDownloadedEvent evt) {
        System.out.println("Downloaded file: " + evt.getFilename());
    }

    public void upload(ScpFileUploadedEvent evt) {
        System.out.println("Uploaded file: " + evt.getFilename());
    }

    public void progress(ScpTransferProgressEvent evt) {
        System.out.println("Transfer progress: " + evt.getFilename() + " " + evt.getTransferredBytes() + " bytes.");
    }

    public void connected(ScpConnectedEvent evt) {
        System.out.println("Connected to host: " + evt.getHost());
    }

    public void disconnected(ScpDisconnectedEvent evt) {
        System.out.println("Disconnected from host: " + evt.getHost());
    }
}