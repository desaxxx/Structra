package com.desoi.structra.model;

import com.desoi.structra.util.Validate;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
public class Selections {

    private Position position1;
    private Position position2;

    public Selections(Position position1, Position position2) {
        Validate.notNull(position1, position2, "Selection positions cannot be null.");
        this.position1 = position1;
        this.position2 = position2;
    }

    public Selections() {}

    @Nullable
    public Position getPosition1() {
        return position1;
    }

    @Nullable
    public Position getPosition2() {
        return position2;
    }

    public void setPosition1(Position position1) {
        Validate.notNull(position1, "Selection position cannot be null.");
        this.position1 = position1;
    }

    public void setPosition2(Position position2) {
        Validate.notNull(position2, "Selection position cannot be null.");
        this.position2 = position2;
    }
}
