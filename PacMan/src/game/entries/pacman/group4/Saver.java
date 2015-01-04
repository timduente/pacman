package game.entries.pacman.group4;

import java.io.IOException;

public class Saver extends Thread{
	
	IMemory memory;
	
	
	public void setMemory(IMemory memory){
		this.memory = memory;
	}
	
	@Override
	public void run(){
		try {
			System.out.println("Write memory Down!");
			memory.writeMemoryToFile("test.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
