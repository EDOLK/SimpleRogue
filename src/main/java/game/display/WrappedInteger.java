package game.display;

public class WrappedInteger{
    private int value;
    public WrappedInteger(int i) {
        this.value = i;
    }
    public void setValue(int value){
        this.value = value;
    }
    public int getValue(){
        return this.value;
    }
    public void increment(){
        this.value = this.value + 1;
    }
    public void decrement(){
        this.value = this.value - 1;
    }
}
