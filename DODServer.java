
public class DODServer {
	public static void main(String[] args){
		if (args.length != 1) {
            System.err.println("Usage: java DODServer <port number>");
            System.exit(1);
        }
		
		int portNumber = Integer.parseInt(args[0]);
	}
}
