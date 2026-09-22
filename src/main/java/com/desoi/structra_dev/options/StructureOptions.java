package com.desoi.structra_dev.options;

import com.google.common.base.Preconditions;

public class StructureOptions implements StructureOptionsView {

    private long delayTicks = 0;
    private long periodTicks = 20;
    private int batchSize = 50;
    private boolean silent = false;

    public StructureOptions() {
    }
    public StructureOptions(StructureOptions options) {
        Preconditions.checkNotNull(options, "options");
        delayTicks(options.delayTicks);
        periodTicks(options.periodTicks);
        batchSize(options.batchSize);
        silent(options.silent);
    }

    @Override
    public long delayTicks() {
        return delayTicks;
    }

    @Override
    public long periodTicks() {
        return periodTicks;
    }

    @Override
    public int batchSize() {
        return batchSize;
    }

    @Override
    public boolean silent() {
        return silent;
    }

    public void delayTicks(long delayTicks) {
        Preconditions.checkArgument(delayTicks >= 0, "delayTicks must be non-negative");
        this.delayTicks = delayTicks;
    }

    public void periodTicks(long periodTicks) {
        Preconditions.checkArgument(periodTicks > 0, "periodTicks must be positive");
        this.periodTicks = periodTicks;
    }

    public void batchSize(int batchSize) {
        Preconditions.checkArgument(batchSize > 0, "batchSize must be positive");
        this.batchSize = batchSize;
    }

    public void silent(boolean silent) {
        this.silent = silent;
    }
}
