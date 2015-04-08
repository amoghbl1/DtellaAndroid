package com.amoghbl1.binary_installer;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import com.amoghbl1.dtellaandroid.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BinaryInstaller {
    Context context;

    String DEBUG_TAG = "myTag";

    public BinaryInstaller (Context context) {
        this.context = context;
    }

    public class BinaryFile {
        public int resId;
        public String fileName;
        private BinaryFile(int resId, String fileName){
            this.resId = resId;
            this.fileName = fileName;
        }
    }
    /*

     */
    public boolean installResources() {
        InputStream inputStream;
        File outFile;
        File appBinHome = context.getDir("bin", Context.MODE_MULTI_PROCESS);
        Resources resources = context.getResources();

        try {
            // Delete the file before we write anything there
            CommandRunner.execCommand("rm -rf ./", appBinHome);

            BinaryFile []binaries = new BinaryFile[2];
            binaries[0] = new BinaryFile(R.raw.busybox, "busybox");
            binaries[1] = new BinaryFile(R.raw.python, "python");
            String output;

            // Changing all the permissions of the files in the app_bin folder
            Log.d("teehee", "no of binaries: "+binaries.length);
            for(int i=0; i<binaries.length ; i++) {
                inputStream = resources.openRawResource(binaries[i].resId);
                outFile = new File(appBinHome, binaries[i].fileName);
                moveBinaryRawResourceToFile(inputStream, outFile);
                output = CommandRunner.execCommand("chmod 6755 ./" + binaries[i].fileName, appBinHome.getAbsoluteFile());
                Log.d(DEBUG_TAG, output + " chmod output: " + CommandRunner.execCommand("ls -la ./"+binaries[i].fileName, appBinHome.getAbsoluteFile()));
            }
        }
        catch (IOException e) {
            Toast.makeText(context, "IOException!", Toast.LENGTH_LONG).show();
            Log.d(DEBUG_TAG, e.getMessage());
        }
        catch (InterruptedException e) {
            Toast.makeText(context, "Command execution Interrupted!", Toast.LENGTH_LONG).show();
            Log.d(DEBUG_TAG, "Interrupted Exception: " + e.getMessage());
        }

        return true;
    }

    /*
    Write from an input stream to an output file
    */
    private void moveBinaryRawResourceToFile(InputStream inputStream, File outFile) throws IOException {
        byte[] buf = new byte[1024];
        int bytecount;
        OutputStream outputStream = new FileOutputStream(outFile.getAbsolutePath());
        while((bytecount = inputStream.read(buf)) > 0) {
            outputStream.write(buf, 0, bytecount);
        }
        inputStream.close();
        outputStream.close();
    }
}
