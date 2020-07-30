import java.io.File;
import java.util.Scanner;

/**
 * @author celestineekoh-ordan
 */
public class RecursiveDirectoryList {

    /**
     * recursively lists the files and directories in a given directory
     *
     * @param directory the directory to be listed
     * @param depth the number of levels deep a directory is from an arbitrary parent
     */
    public void listFilesAndDirectories(File directory, int depth) {
        if (! directory.isDirectory()) {
            if (!directory.exists()) System.out.printf("There is no such directory!");
            else System.out.printf("That file is not a directory");
        } else {
            String[] files = directory.list();

            for (int i = 0; i < files.length; i++) {
                System.out.println("    ".repeat(depth) + files[i]);

                String subDirectoryName = directory.getPath() + "/" + files[i];
                File current = new File(subDirectoryName);

                if (current.isDirectory()) {
                    listFilesAndDirectories(current, depth + 1);
                }
            }
        }
    }

    public static void main(String[] args) {
        RecursiveDirectoryList listUtil = new RecursiveDirectoryList();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a directory name: ");
        String directoryName = scanner.nextLine().trim();
        File directory = new File(directoryName);

        System.out.println("Files in directory \"" + directory + "\":");
        listUtil.listFilesAndDirectories(directory, 1);
    }
}
