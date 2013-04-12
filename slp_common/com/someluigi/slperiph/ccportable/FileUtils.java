package com.someluigi.slperiph.ccportable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.common.io.Files;

public class FileUtils {
	public static final String BACKUP_EXT = ".bck";
	
	public static void makeBackupFile( File file ) throws IOException{	
		String path = file.getAbsolutePath();	
		File target = new File( path + BACKUP_EXT );
				
		int suffix = 0;
		while( target.exists() )
			target = new File( path + BACKUP_EXT + ( ++suffix ) );
		
		Files.copy(file, target);
	}
	
	public static void copyFolder( File from, File to, boolean recursive ) throws IOException{
		to.mkdirs();
	
		for ( File file : from.listFiles() ){
			File dest = new File( to, file.getName() );
			
			if ( file.isDirectory() ){
				
				if ( recursive ) //Copy folders recursively
					copyFolder(file, dest, recursive);
				
			} else {
				
				//File collision, check for equality, and replace if neccesary
				if ( dest.exists() ){
					if ( file.length() == dest.length() )
						continue;
					
					makeBackupFile(dest);
				} else {
					dest.createNewFile();
				}
			
				Files.copy(file, dest);				
			}
		}
	}
	
	public static void unzip( File zip, String folder, File into ) throws IOException{
		into.mkdirs();
		
		ZipInputStream stream = new ZipInputStream( new FileInputStream(zip) );
		ZipEntry entry = stream.getNextEntry();
		
		byte[] buffer = new byte[1024];
		
		while( entry != null ){
			String path = entry.getName();
			
			if ( path.startsWith(folder) ){ //Copy files only from the target folder
				
				//Cut the source folder prefix
				path = path.substring( folder.length(), path.length() ); 
				
				//Target file
				File target = new File( into, path );
				
				if ( target.isDirectory() ){
					
					//Create parent directories
					target.mkdirs();

				} else {
					
					//Overwrite outdated, or conflicted files
					if ( target.exists() ){
						long desiredSize = entry.getSize();
						
						if ( desiredSize > 0 && target.length() != desiredSize )
							makeBackupFile(target);
						
					} else {
						target.createNewFile();
					}
					
					//Copy file contents
					FileOutputStream fOut = new FileOutputStream(target);
					
					int read = 0;
					while( (read = stream.read(buffer)) > -1 )
						fOut.write(buffer, 0, read);
					
					fOut.flush();
					fOut.close();
				}
			}
			
			stream.closeEntry();
			entry = stream.getNextEntry();
		}
		
		stream.close();
	}
	
}
