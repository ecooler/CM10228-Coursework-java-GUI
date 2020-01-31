
public class BotClient {
	public static void main(String[] args){
		if (args.length != 2) {
            System.err.println("Usage: java BotClient <host name> <port number>");
            System.exit(1);
        }
		
		String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
	}
}
