package vuongnguyen.DataLoader;

import java.io.*;

public class TaskReadFile implements Runnable {
    private String fileName;
    private int count;
    public TaskReadFile(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void run() {
        File file = new File(fileName);

        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                DataProcessor.addDataIntoApp(DataProcessor.splitWord(line));

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
