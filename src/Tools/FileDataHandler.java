package Tools;

import Exceptions.IdPrefixException;
import Exceptions.IdPrefixNotFoundException;
import Exceptions.IdPrefixNotMatchException;
import Exceptions.ReaderPrepareFailedException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class FileDataHandler
{
    public static final String DEFAULT_SEPARATOR_REGEX = "\\|";
    public static final String BACKUP_FILE_SUFFIX = "BACKUP";
    private static final int MAXIMUM_PREFIX_LENGTH = 10;
    private final String separatorRegex;
    private final File file;
    public record DataInformation(String[] data,Integer row)
    {
        public boolean isEmpty()
        {
            return (data == null && row == null);
        }
    }
    public String getSeparatorRegex() {return separatorRegex;}
    public String getSeparator() //只做了简单过滤
    {
        if (getSeparatorRegex().charAt(0) == '\\' && getSeparatorRegex().charAt(1) == '\\')
        {
            return getSeparatorRegex().substring(2);
        }
        return getSeparatorRegex();
    }
    public File getFile() {return file;}
    private File getBackUpFile()
    {
        boolean isTouchPoint = false;
        char[] backUpFileChar = new char[260]; //Windows 文件最长260
        final int[] pointer = {0};
        String originalFileName = getFile().getName();
        String originalFilePath = getFile().getParent();
        Consumer<Character> addCharToFile = (character)->
        {
            backUpFileChar[pointer[0]] = character;
            pointer[0]++;
        };
        for (int i = 0; i < originalFileName.length(); i ++)
        {
            if(originalFileName.charAt(i) == '.' && !isTouchPoint)
            {
                for (int j = 0;j < BACKUP_FILE_SUFFIX.length(); j++)
                {
                    addCharToFile.accept(BACKUP_FILE_SUFFIX.charAt(j));
                }
                isTouchPoint = true;
            }
            addCharToFile.accept(originalFileName.charAt(i));
        }
        if (!isTouchPoint)
        {
            for (int j = 0; j < BACKUP_FILE_SUFFIX.length(); j++)
            {
                addCharToFile.accept(BACKUP_FILE_SUFFIX.charAt(j));
            }
        }
        String backUpFileName = new String(backUpFileChar,0,pointer[0]);
        return new File(originalFilePath + File.separator + backUpFileName);
    }

    private BufferedReader prepareReader()
    {
        return _pr(true);
    }
    private BufferedReader prepareReader(boolean skipFirstRow)
    {
        return _pr(skipFirstRow);
    }
    private BufferedReader _pr(boolean skipFirstRow)
    {
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader(this.file));
            if (skipFirstRow) br.readLine();
            return br;
        } catch (FileNotFoundException e)
        {
            System.err.println("File:" + getFile().getName() + "not Found!");
            throw new ReaderPrepareFailedException();
        } catch (IOException e)
        {
            try
            {
                System.err.println(e.getMessage() + "\n" + "Trying to close Reader...");
                br.close();
                System.err.println("Reader close Successfully!");
                throw new ReaderPrepareFailedException(e);
            } catch (IOException _)
            {
                throw new RuntimeException("Try to close Reader but failed",e);
            }
        }
    }
    public FileDataHandler(String filePath,String separator)
    {
        this.file = new File(filePath);
        this.separatorRegex = separator;
    }
    public FileDataHandler(Path filePath)
    {
        this(filePath.toString());
    }

    public FileDataHandler(String filePath)
    {
        this.file = new File(filePath);
        this.separatorRegex = DEFAULT_SEPARATOR_REGEX;
    }
    public String findPrefixInSpecificRow(int row)
    {
        try
        {
            return prefixFinder(Objects.requireNonNull(getDataFromSpecificRow(row))[0]);
        }
        catch (IdPrefixException e)
        {
            System.err.println("Prefix cannot found in this file!");
            return null;
        }
    }
    public String findPrefixStrict()
    {
        try(BufferedReader fileReader = prepareReader())
        {
            String prefix = null;
            for(int i = 0; i < getFileRow(); i++)
            {
                String data = fileReader.readLine();
                if (prefix == null)
                {
                    prefix = prefixFinder(dataToArray(data)[0]);
                    continue;
                }
                if (!prefix.equals( prefixFinder(dataToArray(data)[0])))
                    throw new IdPrefixNotMatchException("Id prefix is not all matched in file:"+file.getName());
            }
            if (prefix == null) throw new IdPrefixNotFoundException("Id prefix not found! in file:"+file.getName());
            return prefix;
        }
        catch (IOException e)
        {
            System.err.println("Something happen while getting data\n" + e.getMessage());
            throw new ReaderPrepareFailedException(e);
        }
    }

    public static String prefixFinder(String id)
    {
        if (id == null || id.isEmpty()) throw new IdPrefixNotFoundException("The id is empty!");
        char[] prefix = new char[MAXIMUM_PREFIX_LENGTH];
        int prefixPointer = 0;
        for (int i = 0;i < id.length(); i ++)
        {
            if (Character.isLetter(id.charAt(i)))
            {
                if (prefixPointer >= MAXIMUM_PREFIX_LENGTH)
                    throw new IllegalArgumentException
                            ("prefix length is larger than maximum prefix length\n" +
                            "Please check your file Or change the maximum prefix length");
                prefix[prefixPointer] = id.charAt(i);
                prefixPointer ++;
                continue;
            }
            if (Character.isDigit(id.charAt(i))) {break;}
        }
        if (prefixPointer == 0) throw new IdPrefixNotFoundException("No Prefix Found in this id!");
        return new String(prefix,0,prefixPointer);
    }



    private List<String[]> dataToArrayList(List<String> data)
    {
        List<String[]> list = new ArrayList<String[]>();
        for (String rowData : data)
        {
            list.add(dataToArray(rowData));
        }
        return list;
    }
    private String[] dataToArray(String data)
    {
        if (data == null) throw new IllegalArgumentException("Data should not be null!");
        return data.split(getSeparatorRegex());
    }

    private String ArrayToData (String[] array)
    {
        return String.join(getSeparator(),array);
    }

    public DataInformation getDataInformationFromSpecificId (String id)
    {
        try (BufferedReader fileReader = prepareReader())
        {
            int row = 1;
            for (String data; (data = fileReader.readLine()) != null; )
            {
                String[] arrayData = dataToArray(data);
                if (!arrayData[0].equals(id))
                {
                    row++;continue;
                }
                return new DataInformation(arrayData, row);
            }
            return new DataInformation(null,null);
        } catch (IOException e)
        {
            System.err.println("Something happen while getting data\n" + e.getMessage());
            throw new ReaderPrepareFailedException(e);
        }
    }

    public String[] getDataFromSpecificRow(int row)
    {
        try (BufferedReader fileReader = prepareReader())
        {
            for (int i = 0; i < row-1; i++)
            {
                fileReader.readLine();
            }
            String data = fileReader.readLine();
            if (data == null)
                throw new IndexOutOfBoundsException("Cannot get data : row %d is out of bounds from file %S".formatted(row,file.getName()));
            return dataToArray(data);
        }
        catch (IOException e)
        {
            System.err.println("Something happen while getting data\n" + e.getMessage());
            throw new ReaderPrepareFailedException(e);
        }
    }
    private List<String> cacheFile() throws IOException
    {
        try (BufferedReader fileReader = prepareReader(false))
        {
            return new ArrayList<>(fileReader.readAllLines());
        }
    }
    private  void writeFile(List<String> fileData)
    {
        File backUpFile = getBackUpFile();
        try
        {
            Files.copy(file.toPath(), backUpFile.toPath());
        } catch (IOException e)
        {
            System.err.println(e.getMessage());
            System.err.println("BackUp File Failed,for secure reason,cannot write file");
            backUpFile.delete();
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
        {
            for (int i = 0;i < fileData.size();i++)
            {
                writer.write(fileData.get(i));
                if (i == fileData.size()-1) continue;
                writer.newLine();
            }
            backUpFile.delete();
        } catch (IOException e)
        {
            System.err.println("Got Error when writing file,copying backUp File to original File...");
            try
            {
                Files.copy(backUpFile.toPath(), file.toPath());
                backUpFile.delete();
            } catch (IOException e2)
            {
                System.err.println(e2.getMessage());
                System.err.println("Got Error when copying backUp File to original File");
                System.err.println("System must forced close to save the data File");
                System.exit(1);
            }
        }

    }
    private int getFileRow()
    {
        try (BufferedReader fileReader = prepareReader())
        {
            int row = 0;
            while (fileReader.readLine() != null)
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
    public void deleteRow (int row)
    {
        if (row > getFileRow()) throw new IllegalArgumentException("row should not be bigger than file row");
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
        originalFile.remove(row);
        writeFile(originalFile);
    }
    public void updateData(String data,int row)
    {
        try
        {
            List<String> dataToWrite = cacheFile();
            if (row >= dataToWrite.size())
                throw new IllegalArgumentException("updateData failed : row %d is bigger than file row %d".formatted(row,dataToWrite.size()));
            dataToWrite.set(row,data);
            writeFile(dataToWrite);
        } catch(IOException e)
        {
            System.err.printf("updateData failed : Error when caching data: %s%n", e.getMessage());
        }
    }

    public void addData(String data)
    {
        try
        {
            List<String> dataToWrite = cacheFile();
            dataToWrite.add(data);
            writeFile(dataToWrite);
        } catch(IOException e)
        {
            System.err.printf("addData failed : Error when caching data: %s%n", e.getMessage());
        }
    }
}
