package com.cbi.coollink.cli;

public interface CliProgram {
    /**Process ongoing program operations
     */
    void tick();

    /**Get if this program is still running
     * @return true if this program is still running on the tick method
     */
    boolean isProgramRunning();
}
