package export;

import lombok.Getter;

public class Instruction {

    @Getter
    private final InstructionType type;

    @Getter
    private final int index;

    @Getter
    private final int[][][] data;

    public Instruction(InstructionType type)
    {
        this.type = type;
        this.index = -1;
        this.data = new int[0][0][0];
    }

    public Instruction(InstructionType type, int index)
    {
        this.type = type;
        this.index = index;
        this.data = new int[0][0][0];
    }

    public Instruction(InstructionType type, int index, int[][][] data)
    {
        this.type = type;
        this.index = index;
        this.data = data;
    }

}
