package com.arcing.naviaux.music;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import javax.swing.SwingUtilities;

import javafx.embed.swing.JFXPanel;

import com.He.W.onebone.Circuit.Cu.exception.ParseException;
import com.arcing.naviaux.music.library.Library;
import com.arcing.naviaux.music.player.PlayingManager;
import com.arcing.naviaux.music.window.MusicSelectionWindow;

public class Launcher {
	public static void main(String[] args) throws InterruptedException, IOException, ParseException {
		System.out.println("Begining Program Excecution");
		System.out.println("Creating Music Selection Window");
		final CountDownLatch latch = new CountDownLatch(1);
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		        new JFXPanel(); // initializes JavaFX environment
		        latch.countDown();
		    }
		});
		latch.await();
		Library.createLibrary();
		PlayingManager.buildRepeatList();
		MusicSelectionWindow msw = new MusicSelectionWindow();
		msw.setVisible(true);
	}
}
