package es.upm.etsisi.test;

import es.upm.etsisi.poo.BaseProduct;
import es.upm.etsisi.poo.Ticket;
import es.upm.etsisi.poo.TimedProduct;
import es.upm.etsisi.poo.exceptions.DuplicateItemException;
import es.upm.etsisi.poo.exceptions.FullCollectionException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

public class TicketTest extends BaseTest {

    private static final DateTimeFormatter TEST_FMT = DateTimeFormatter.ofPattern("YY-MM-dd-HH:mm");

    private static String openComposedId(int id) {
        return Ticket.TEST_NOW_DATE.format(TEST_FMT) + "-" + String.format("%05d", id);
    }

    private static String closedComposedId(int id) {
        return String.format("%05d", id) + "-" + Ticket.TEST_NOW_DATE.format(TEST_FMT);
    }

    private BaseProduct baseProduct(int id, String name, double price, String category, int maxPers, boolean personalized) {
        return new BaseProduct(id, name, price, category, maxPers, personalized);
    }

    // OJO: TimedType solo admite "MEETING" o "FOOD" en tu proyecto.
    private TimedProduct timedProduct(int id, String name, double price, int maxParticipants, String type, LocalDateTime expiration) {
        return new TimedProduct(id, name, price, maxParticipants, type, expiration);
    }

    @Nested
    class ConstructorAndInitialState {

        @Test
        void constructor_negativeId_throws() {
            assertThrows(IllegalArgumentException.class, () -> new Ticket(-1));
        }

        @Test
        void constructor_initialState_ok() {
            Ticket t = new Ticket(42);

            assertAll(
                    () -> assertTrue(t.isOpen()),
                    () -> assertTrue(t.isEmpty()),
                    () -> assertEquals(42, t.getId()),
                    () -> assertEquals(Ticket.TEST_NOW_DATE, t.getDateOpened()),
                    () -> assertNull(t.getDateClosed()),
                    () -> assertEquals(openComposedId(42), t.getComposedId())
            );
        }
    }

    @Nested
    class ComposedId {

        @Test
        void composedId_whenOpen_includesOpenDateAnd5Digits() {
            Ticket t = new Ticket(7);
            assertEquals(openComposedId(7), t.getComposedId());
        }

        @Test
        void composedId_whenClosed_isIdThenCloseDate() {
            Ticket t = new Ticket(7);
            t.tryClose(); // sin productos, debe cerrar

            assertAll(
                    () -> assertFalse(t.isOpen()),
                    () -> assertNotNull(t.getDateClosed()),
                    () -> assertEquals(closedComposedId(7), t.getComposedId())
            );
        }
    }

    @Nested
    class AddProduct_BaseProduct {

        @Test
        void addBaseProduct_firstTime_discountAppearsForClothesWhenAmountGt1() throws Exception {
            Ticket t = new Ticket(1);
            // CLOTHES: descuento 7%
            BaseProduct p = baseProduct(10, "Alpha", 10.0, "CLOTHES", 0, false);

            t.addProduct(p, 2, new String[0]); // 2 unidades => aplica descuento de categoría

            String s = t.summaryString();
            assertTrue(s.contains("Ticket : " + openComposedId(1)));
            assertTrue(s.contains("**discount -"), "En CLOTHES con 2 unidades debe aparecer descuento");
            assertTrue(s.contains("  Total price: 20.0"));
            // descuento: 7% de 10.0 = 0.7 por item; por 2 => 1.4
            assertTrue(s.contains("  Total discount: 1.4"));
            assertTrue(s.contains("  Final Price: 18.6"));
        }

        @Test
        void addBaseProduct_duplicateSamePersonalizations_incrementsAmount() throws Exception {
            Ticket t = new Ticket(1);
            BaseProduct p = baseProduct(10, "Alpha", 10.0, "CLOTHES", 0, false);

            t.addProduct(p, 1, new String[0]);
            t.addProduct(p, 1, new String[0]);

            String s = t.summaryString();
            assertTrue(s.contains("  Total price: 20.0"), "2 unidades * 10.0 = 20.0");
        }

        @Test
        void addBaseProduct_newProduct_overflow_throwsFullCollectionException() throws Exception {
            Ticket t = new Ticket(1);
            BaseProduct p1 = baseProduct(10, "Alpha", 1.0, "CLOTHES", 0, false);
            BaseProduct p2 = baseProduct(11, "Beta", 1.0, "CLOTHES", 0, false);

            t.addProduct(p1, Ticket.MAX_PRODUCTS, new String[0]); // llena totalAmount=100

            assertThrows(FullCollectionException.class, () -> t.addProduct(p2, 1, new String[0]));
        }

        @Test
        void addBaseProduct_duplicateOverflow_doesNothing_noException() throws Exception {
            Ticket t = new Ticket(1);
            BaseProduct p1 = baseProduct(10, "Alpha", 1.0, "CLOTHES", 0, false);

            t.addProduct(p1, 99, new String[0]);
            // duplicado: si se pasa de MAX, tu código NO lanza, simplemente no incrementa
            t.addProduct(p1, 2, new String[0]);

            String s = t.summaryString();
            assertTrue(s.contains("  Total price: 99.0"), "Debe quedarse en 99.0, no subir a 101.0");
        }

        @Test
        void addBaseProduct_sameIdDifferentPersonalizations_countsAsDifferentItems() throws Exception {
            Ticket t = new Ticket(1);
            // CLOTHES permite personalizaciones (máx. 5)
            BaseProduct p = baseProduct(10, "Alpha", 10.0, "CLOTHES", 5, true);

            t.addProduct(p, 1, new String[]{"A"});
            t.addProduct(p, 1, new String[]{"B", "C"}); // distinta personalización => no es duplicado

            String s = t.summaryString();
            assertTrue(s.contains("personalizationList:[A]"));
            assertTrue(s.contains("personalizationList:[B, C]"));
            // Precio efectivo: 10*(1+0.1*1)=11 y 10*(1+0.1*2)=12 => total 23.0
            assertTrue(s.contains("  Total price: 23.0"));
        }

        @Test
        void addBaseProduct_personalization_affectsPriceAndDiscount() throws Exception {
            Ticket t = new Ticket(1);
            BaseProduct p = baseProduct(10, "Alpha", 10.0, "CLOTHES", 5, true);

            t.addProduct(p, 2, new String[]{"X", "Y"}); // 2 pers => +20% => 12.0 cada uno

            String s = t.summaryString();
            // total price = 12.0*2 = 24.0
            assertTrue(s.contains("  Total price: 24.0"));
            // descuento CLOTHES 7%: 0.84 por item => 1.68
            assertTrue(s.contains("  Total discount: 1.68"));
            assertTrue(s.contains("  Final Price: 22.32"));
        }
    }

    @Nested
    class AddProduct_TimedProduct {

        @Test
        void addTimedProduct_amountOverMaxParticipants_notAdded_ticketStaysEmpty() throws Exception {
            Ticket t = new Ticket(1);
            TimedProduct tp = timedProduct(20, "Event", 50.0, 3, "MEETING", LocalDateTime.now().plusDays(10));

            t.addProduct(tp, 4, new String[0]); // 4 > maxParticipants(3) => no añade nada
            assertTrue(t.isEmpty());
        }

        @Test
        void addTimedProduct_duplicate_throwsDuplicateItemException() throws Exception {
            Ticket t = new Ticket(1);
            TimedProduct tp = timedProduct(20, "Event", 50.0, 10, "MEETING", LocalDateTime.now().plusDays(10));

            t.addProduct(tp, 2, new String[0]);
            assertThrows(DuplicateItemException.class, () -> t.addProduct(tp, 1, new String[0]));
        }

        @Test
        void addTimedProduct_countsAsOneTowardMaxProducts() throws Exception {
            Ticket t = new Ticket(1);

            for (int i = 0; i < Ticket.MAX_PRODUCTS; i++) {
                TimedProduct tp = timedProduct(1000 + i, "E" + i, 1.0, 100, "MEETING", LocalDateTime.now().plusDays(30));
                t.addProduct(tp, 10, new String[0]); // cuenta como 1 en totalAmount
            }

            TimedProduct extra = timedProduct(9999, "EXTRA", 1.0, 100, "MEETING", LocalDateTime.now().plusDays(30));
            assertThrows(FullCollectionException.class, () -> t.addProduct(extra, 1, new String[0]));
        }
    }

    @Nested
    class RemoveProduct {

        @Test
        void removeProduct_existing_returnsTrue_andEmptiesTicket() throws Exception {
            Ticket t = new Ticket(1);
            BaseProduct p = baseProduct(10, "Alpha", 10.0, "CLOTHES", 0, false);

            t.addProduct(p, 1, new String[0]);
            assertTrue(t.removeProduct(10));
            assertTrue(t.isEmpty());
        }

        @Test
        void removeProduct_nonExisting_returnsFalse() {
            Ticket t = new Ticket(1);
            assertFalse(t.removeProduct(999));
        }

        @Test
        void removeProduct_freesCapacity_baseProduct() throws Exception {
            Ticket t = new Ticket(1);
            BaseProduct p1 = baseProduct(10, "Alpha", 1.0, "CLOTHES", 0, false);
            BaseProduct p2 = baseProduct(11, "Beta", 1.0, "CLOTHES", 0, false);

            t.addProduct(p1, 100, new String[0]);
            assertThrows(FullCollectionException.class, () -> t.addProduct(p2, 1, new String[0]));

            assertTrue(t.removeProduct(10));
            assertDoesNotThrow(() -> t.addProduct(p2, 100, new String[0]));
        }
    }

    @Nested
    class TryClose {

        @Test
        void tryClose_withExpiredTimedProduct_throws_andKeepsOpen() throws Exception {
            Ticket t = new Ticket(1);
            // Expira en el pasado; además getExpirationDate resta horas, así que sigue siendo pasado.
            TimedProduct expired = timedProduct(20, "Expired", 10.0, 10, "MEETING", LocalDateTime.now().minusDays(2));

            t.addProduct(expired, 1, new String[0]);

            assertThrows(DateTimeException.class, t::tryClose);
            assertTrue(t.isOpen());
        }

        @Test
        void tryClose_ok_closesAndSetsDateClosed() throws Exception {
            Ticket t = new Ticket(1);
            TimedProduct ok = timedProduct(20, "Ok", 10.0, 10, "MEETING", LocalDateTime.now().plusDays(30));

            t.addProduct(ok, 1, new String[0]);
            t.tryClose();

            assertAll(
                    () -> assertFalse(t.isOpen()),
                    () -> assertNotNull(t.getDateClosed()),
                    () -> assertEquals(closedComposedId(1), t.getComposedId())
            );
        }

        @Test
        void tryClose_makesDefensiveCopy_productsNotAffectedByExternalMutation() throws Exception {
            Ticket t = new Ticket(1);
            BaseProduct p = baseProduct(10, "Alpha", 10.0, "CLOTHES", 5, true);
            String[] pers = new String[]{"X"};

            t.addProduct(p, 1, pers);
            t.tryClose(); // clona productos y personalizaciones

            // mutaciones externas
            p.setName("MUTATED");
            pers[0] = "MUTATED_PERS";

            String s = t.summaryString();
            assertTrue(s.contains("name:'Alpha'"), "El ticket debería conservar el nombre original tras cerrar");
            assertTrue(s.contains("personalizationList:[X]"), "El ticket debería conservar la personalización original tras cerrar");
        }
    }

    @Nested
    class CompareTo {

        @Test
        void compareTo_ordersById() {
            Ticket a = new Ticket(10);
            Ticket b = new Ticket(20);

            assertTrue(a.compareTo(b) < 0);
            assertTrue(b.compareTo(a) > 0);
            assertEquals(0, a.compareTo(new Ticket(10)));
        }
    }
}
