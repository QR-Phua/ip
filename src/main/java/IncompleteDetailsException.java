public class IncompleteDetailsException extends Exception{
    public IncompleteDetailsException() {
        super("""
                +––––––––––––––––––––––––––––––––––––––––––––––+
                Incomplete details to create task!
                +––––––––––––––––––––––––––––––––––––––––––––––+""");
    }
}
