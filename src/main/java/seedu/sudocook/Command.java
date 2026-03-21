package seedu.sudocook;

public class Command {
    public boolean isExit;
    public Command(boolean isExit){
        this.isExit = isExit;
    }
    public void execute(){

    }
    public void execute(RecipeBook list){

    }

    public void execute(Inventory inventory){

    }

    public void execute(Recipe recipe, Inventory inventory){

    }

    public int getIndex(){
        return 0;
    }

}
