package Utility.DataBase.Daos.Users;

// Service interface
interface MessageService {
    String getMessage();
}

// Concrete implementation of the Service interface
class EmailService implements MessageService {
    @Override
    public String getMessage() {
        return "Email message";
    }
}

// Another implementation of the Service interface
class SMSService implements MessageService {
    @Override
    public String getMessage() {
        return "SMS message";
    }
}

// Client class that depends on MessageService through constructor injection
public class MessageClient {
    private final MessageService messageService;

    // Constructor injection
    public MessageClient(MessageService messageService) {
        this.messageService = messageService;
    }

    public void processMessage() {
        String message = messageService.getMessage();
        System.out.println("Processing message: " + message);
    }

    public static void main(String[] args) {
        // Creating instances of services
        MessageService emailService = new EmailService();
        MessageService smsService = new SMSService();

        // Injecting dependencies through constructor
        MessageClient emailClient = new MessageClient(emailService);
        MessageClient smsClient = new MessageClient(smsService);

        // Processing messages
        emailClient.processMessage(); // Output: Processing message: Email message
        smsClient.processMessage();   // Output: Processing message: SMS message
    }
}
