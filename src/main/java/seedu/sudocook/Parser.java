package seedu.sudocook;

import static seedu.sudocook.SudoCook.DELETE_R_PREFIX;

public class Parser {
    private final Ui ui;

    public Parser(Ui ui) {
        this.ui = ui;
    }

    public Command parse(String input){
        Command C;
        if(input.startsWith("delete-r")){
            int index = Integer.parseInt(input.substring(DELETE_R_PREFIX).trim());
            C = new DeleteCommand(index);
        } else if (input.startsWith("list-r")){
            C = new ListCommand();
        } else{
            C = new Command(false);
            ui.printError("I don't recognise that command!");
        }
        return C;

    }


}
