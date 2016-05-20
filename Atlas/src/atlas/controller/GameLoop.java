package atlas.controller;

import atlas.model.*;
import atlas.view.*;

/**
 * The GameLoop is the main Thread of Simulation, that synchronizes Model and View
 * @author andrea
 */

public class GameLoop extends Thread {

    private final static int FPS = 50;
    private final static int SKIP_TICKS = 1000 / FPS;
    private final static int MAX_FRAMESKIP = 10;
    private volatile int loop;
    private long next_game_tick;
    private StatusSim status;
    private ModelInterface model = new Model();
    private ViewInterface view;
    
    /**
     * Creates new GameLoop and sets the status of Simulation to Stop
     * @param v
     * The ViewInterface object
     */
    public GameLoop() { 
        this.status = StatusSim.STOPPED;
    }
    
    
    public void run() {
        while (!status.equals(StatusSim.EXIT)) {
            this.next_game_tick = System.currentTimeMillis();
            while (status.equals(StatusSim.RUNNING)) {
                long lastFrame = System.currentTimeMillis();
                this.loop = 0;
                while ((System.currentTimeMillis() > this.next_game_tick) && (this.loop < MAX_FRAMESKIP)) {
                    this.model.updateSim(6);
                    this.next_game_tick += SKIP_TICKS;
                    this.loop++;
                }
                // sleep for 1 ms if too fast
                while (System.currentTimeMillis() - lastFrame < SKIP_TICKS) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                  // quando sono in pausa passo al model lo Status
                }
                // timeSLF = time since last frame
                long timeSLF = System.currentTimeMillis() - lastFrame;
                // rendering();
                this.view.render(model.getBodiesToRender());
                long FPS = 1000 / timeSLF;
                System.out.println("timeSLF = " + timeSLF + " --> FPS  = " + FPS);
            }
        }
    }
    
    /**
     * This method sets the Status
     * @param status
     * StatusSim object
     */
    private synchronized void setStatus(StatusSim status) {
        this.status = status;
    }
    
    /**
     * This method sets the Status to Running calling setStatus()
     */
    public void setRunning() {
        if (!this.status.equals(StatusSim.RUNNING)) {
            this.setStatus(StatusSim.RUNNING);
        }
    }
    
    /**
     * This method sets the Status to Stopped calling setStatus()
     */
    public void setStopped() {
        if (!this.status.equals(StatusSim.STOPPED)) {
            this.setStatus(StatusSim.STOPPED);
        }
    }
    
    /**
     * This method sets the Status to Stopped calling setStatus()
     */
    public void setExit() {
        this.setStatus(StatusSim.EXIT);
    }
    
    /**
     * This method returns the current Status
     * @return
     */
    public synchronized StatusSim getStatus() {
        return this.status;
    }
    
    public void setView(ViewInterface v) {
        this.view = v;
    }

}
