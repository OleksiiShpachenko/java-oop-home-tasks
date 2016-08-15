package com.shpach.filescopy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;

import com.shpach.filescopy.IOUtils.InputParamNotDirExeption;

public class MultyThreadCopy implements Runnable {
	private File dirFrom;
	private File dirTo;
	FilenameFilter fnf;
	private int threadNumber;
	Thread th;

	public MultyThreadCopy(File dirFrom, File dirTo, FilenameFilter fnf, int threadNumber)
			throws InputParamNotDirExeption {
		super();
		this.dirFrom = dirFrom;
		this.dirTo = dirTo;
		this.fnf = fnf;
		this.threadNumber = threadNumber;
		if (dirFrom.exists() && dirTo.exists()) {
			if (!dirFrom.isDirectory() || !dirTo.isDirectory())
				throw new IOUtils.InputParamNotDirExeption();

		} else
			throw new IOUtils.InputParamNotDirExeption();

		th = new Thread(this);
		th.start();

	}

	public MultyThreadCopy(File dirFrom, File dirTo, FilenameFilter fnf) throws InputParamNotDirExeption {
		this(dirFrom, dirTo, fnf, Runtime.getRuntime().availableProcessors());

	}

	public MultyThreadCopy() {
		super();
	}

	@Override
	public void run() {
		System.out.println("Start copy directory " + this.dirFrom + " to " + this.dirTo);
		File[] fileList = dirFrom.listFiles(fnf);
		Thread[] trArr = new Thread[this.threadNumber];
		for (int i = 0; i < fileList.length; i += trArr.length) {
			for (int j = 0; j < trArr.length; j++) {
				if ((i + j) < fileList.length) {
					trArr[j] = new Thread(new ThreadCopy(fileList[i + j],
							new File(dirTo.getAbsolutePath() + "/" + fileList[i + j].getName())));
					trArr[j].start();
				} else
					trArr[j] = null;
			}

			for (int j = 0; j < trArr.length; j++) {
				try {
					if (trArr[j] == null)
						continue;
					trArr[j].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		System.out.println("Done ");

	}

	private class ThreadCopy implements Runnable {
		private File fileFrom;
		private File fileTo;

		public ThreadCopy(File fileFrom, File fileTo) {
			super();
			this.fileFrom = fileFrom;
			this.fileTo = fileTo;
		}

		public ThreadCopy() {
			super();
		}

		@Override
		public void run() {
			try {
				IOUtils.copyFile(this.fileFrom, this.fileTo);
				System.out.println("File " + this.fileFrom + "successfully copied to " + this.fileTo);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
