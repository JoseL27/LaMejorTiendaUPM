/* date = December 28th 2025 9:53 pm */

package es.upm.etsisi.poo;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServiceProduct extends InventoryItem {
    
    public static final float SERVICE_DISCOUNT = 0.15f;
    public static final DateTimeFormatter EXPIRATION_DATE_FORMAT = DateTimeFormatter.ofPattern("EEE LLL HH:mm:ss zzz yyyy");
    
    public enum Category {
        TRANSPORT, SHOW, INSURANCE,
    }
    
    private Category category;
    private LocalDateTime expirationDate;
    
    
    public ServiceProduct(int id, Category category, LocalDateTime expirationDate) {
        super(id);
        
        if (id <= 0) throw new IllegalArgumentException("Service id must be greater or equal to one, got " + id);
        if (category == null) throw new IllegalArgumentException("Category can not be null");
        if (expirationDate == null) throw new IllegalArgumentException("Expiration can not be null");
        if (expirationDate.isBefore(LocalDateTime.now())) 
            throw new IllegalArgumentException("Expiration can not be in the past, got " + expirationDate.format(EXPIRATION_DATE_FORMAT));
        
        this.category = category;
        this.expirationDate = expirationDate;
    }
    
    
    public static ServiceProduct newFromId(String id, Category category, LocalDateTime expirationDate) throws IllegalArgumentException {
        int idNum = 0;
        
        if (id != null && id.length() > 0) {
            char last = id.charAt(id.length()-1);
            if (last == 'S' || last == 's') {
                try {
                    idNum = Integer.valueOf(id.substring(1));
                } catch (NumberFormatException e) {
                    idNum = 0;
                }
            }
        } 
        
        if (idNum == 0) {
            throw new IllegalArgumentException("Expected id to be greater than 0 and have an S at the end, got " + id);
        }
        
        return new ServiceProduct(idNum, category, expirationDate);
    }
    
    public Category getCategory() {
        return this.category;
    }
    
    public LocalDateTime getExpirationDate() {
        return this.expirationDate;
    }
    
    @Override
        public String toString() {
        return String.format("{class:ProductService, id:%d, category:INSURANCE, expiration:%s}",
                             this.id, category.toString(), this.expirationDate.format(EXPIRATION_DATE_FORMAT));
        
    }
    
    @Override
        public boolean duplicateOf(InventoryItem product) {
        return false;
    }
    
}
