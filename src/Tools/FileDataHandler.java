package Tools;

import entities.BaseEntity;

import java.io.*;
import java.util.function.Function;

public class FileDataHandler
{
    private File file;
    private BufferedReader fileReader;

    public void prepareReader() throws FileNotFoundException
    {
        if (fileReader != null)
        {
            try
            {
                fileReader.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        fileReader = new BufferedReader(new FileReader(this.file));
    }

    public FileDataHandler(String filePath) throws FileNotFoundException
    {
        this.file = new File(filePath);
        try
        {
            prepareReader();
        }
        catch (FileNotFoundException e)
        {
            IO.println("File:" + file.getName() + "not Found!");
            throw new FileNotFoundException(e.getMessage());
        }
    }

                                                            //类型擦除导致只能让T自己把方法丢给这里
    public <T extends BaseEntity> T getEntity(int rowInFile, Function<String[],T> constructorFunctionForT)
    {
        try
        {
            prepareReader();
            for(int i = 0;i < rowInFile - 1;i ++) { fileReader.readLine();}
            String[] data = fileReader.readLine().split("\\|");
            return constructorFunctionForT.apply(data);
        }
        catch (IOException _) {return null;}
    }
}
