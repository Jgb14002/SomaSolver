package models;

import export.InstructionType;
import lombok.Data;
import lombok.Getter;

@Data
public class InstructionEntry {

    @Getter
    private final InstructionType instruction;

    @Getter
    private final int piece;

}
