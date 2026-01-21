public class InvalidTaskItemException extends Exception {
    public InvalidTaskItemException() {
        super("""
                +––––––––––––––––––––––––––––––––––––––––––––––+
                Invalid Task!
                +––––––––––––––––––––––––––––––––––––––––––––––+""");

    }
}


