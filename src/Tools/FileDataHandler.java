package Tools;

import Exceptions.IdPrefixNotMatchException;
import Exceptions.ReaderPrepareFailedException;
import entities.BaseEntity;

import java.io.*;
import java.util.List;
import java.util.function.Function;

public class FileDataHandler
{
    private final File file;
    private BufferedReader fileReader;

    public void prepareReader()
    {
        if (fileReader != null)
        {
            try
            {
                fileReader.close();
            }
            catch (IOException _){}
        }
        try
        {
            fileReader = new BufferedReader(new FileReader(this.file));
        }
        catch (FileNotFoundException e)
        {
            System.err.println("File:" + file.getName() + "not Found!");
            throw new ReaderPrepareFailedException();
        }
    }

    public FileDataHandler(String filePath) throws FileNotFoundException
    {
        this.file = new File(filePath);
        prepareReader();
    }

                                                            //类型擦除导致只能让T自己把方法丢给这里
    public <T extends BaseEntity> T getEntity(int rowInFile, Function<String[],T> constructorFunctionForT)
    {
        try
        {
            prepareReader();
            for(int i = 0;i < rowInFile - 1;i ++) { fileReader.readLine();}
            String[] data = fileReader.readLine().split("\\|");
            try
            {
                return constructorFunctionForT.apply(data);
            }
            catch (IdPrefixNotMatchException e)
            {
                System.err.println("Please Check Your File,The idPrefix didn't match!\n" + e.getMessage());
                return null;
            }
        }
        catch (IOException _) {return null;}
    }
    public List<String> cacheFile() throws IOException
    {
        prepareReader();
        return this.fileReader.readAllLines();
    }
    public int getFileRow()
    {
        prepareReader();
        try
        {
            int row = 0;
            while (this.fileReader.readLine() != null)
            {
                row += 1;
            }
            return row;
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }
    public void DeleteRow (int row)
    {
        if (row+1 > getFileRow()) throw new IllegalArgumentException("row should not be bigger than file row");
        List<String> originalFile = null;
        try
        {
            originalFile = cacheFile();
        }
        catch (IOException e)
        {
            System.err.println("Error while delete row");
        }
        if (originalFile == null) return;
        originalFile.remove(row + 1);
    }
}
