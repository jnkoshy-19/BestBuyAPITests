package bestbuyApiPlayground.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtilities {
	
	public String readFile(String path) throws IOException {
		 return new String(Files.readAllBytes(Paths.get(path)));
	}

}
