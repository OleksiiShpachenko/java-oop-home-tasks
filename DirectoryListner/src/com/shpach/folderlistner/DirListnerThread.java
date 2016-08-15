package com.shpach.folderlistner;

import java.io.File;

public class DirListnerThread implements Runnable {
	private File dir;
	private File[] files;

	public DirListnerThread(File dir) {
		super();
		this.dir = dir;
		files = dir.listFiles();
	}

	public DirListnerThread() {
		super();
	}

	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		for (; !Thread.currentThread().isInterrupted();) {
			try {
				Thread.currentThread().sleep(1000);
				File[] fileNew = dir.listFiles();
				int fileMaxLength = files.length;
				if (fileNew.length > files.length)
					fileMaxLength = fileNew.length;
				boolean deleteFile, newFile;

				for (int i = 0; i < fileMaxLength; i++) {
					// File file = fileNew[i];
					deleteFile = true;
					newFile = true;
					for (int j = 0; j < fileMaxLength; j++) {
						if (i < files.length && j < fileNew.length) {
							if (files[i].getName().equals(fileNew[j].getName()))
								deleteFile = false;
						}
						if (i < fileNew.length && j < files.length) {
							if (fileNew[i].getName().equals(files[j].getName()))
								newFile = false;
						}
					}
					if (deleteFile && i < files.length) {
						System.out.println("File " + files[i].getName() + " deleted");
					}
					if (newFile && i < fileNew.length) {
						System.out.println("File " + fileNew[i].getName() + " have added");
					}
				}
				files=fileNew;

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				break;
			}
		}

	}

}
