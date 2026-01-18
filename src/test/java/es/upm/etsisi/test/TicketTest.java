package es.upm.etsisi.test;

import es.upm.etsisi.poo.*;
import es.upm.etsisi.poo.exceptions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class TicketTest extends BaseTest {
	
    private static String openComposedId(int id, boolean isIdCustom) {
		String result = null;
		
		if (isIdCustom) {
			result = String.format("%05d", id);
			
		} else {
			result = String.format("%s-%05d", App.now().format(Ticket.ID_DATE_FORMAT), id);
		}
		
		return result;
    }
	
	private static String openComposedId(int id) {
		return openComposedId(id, true);
	}
	
    private static String closedComposedId(int id, boolean isIdCustom) {
		String result = String.format("%s-%s", openComposedId(id, isIdCustom), 
									  App.now().format(Ticket.ID_DATE_FORMAT));
		return result;
    }
	
	private static String closedComposedId(int id) {
		return closedComposedId(id, true);
	}
	
	
    @Nested
		class ConstructorAndInitialState {
		
        @Test
			void constructor_negativeId_throws() {
            assertThrows(IllegalArgumentException.class, () -> new ProductTicket(-1, false));
        }
		
        @Test
			void constructor_initialNonCustomIdState_ok() {
            Ticket t = new ProductTicket(42, false);
			
			assertTrue(t.isOpen());
			assertTrue(t.isEmpty());
			assertEquals(42, t.getId());
			assertNull(t.getDateClosed());
			assertEquals(openComposedId(42, false), t.getComposedId());
        }
		
		@Test
			void constructor_initialCustomIdState_ok() {
            Ticket t = new ProductTicket(42, true);
			
			assertTrue(t.isOpen());
			assertTrue(t.isEmpty());
			assertEquals(42, t.getId());
			assertNull(t.getDateClosed());
			assertEquals(openComposedId(42, true), t.getComposedId());
        }
    }
	
    @Nested
		class ComposedId {
		
        @Test
			void composedId_whenOpenNonCustom_includesOpenDateAnd5Digits() {
            Ticket t = new ProductTicket(7, false);
            assertEquals(openComposedId(7, false), t.getComposedId());
        }
		
		
        @Test
			void composedId_whenOpenCustom_includesOpenDateAnd5Digits() {
            Ticket t = new ProductTicket(7, true);
            assertEquals(openComposedId(7, true), t.getComposedId());
        }
		
        @Test
			void composedId_whenClosedNonCustom_isIdThenCloseDate() {
            final Ticket t = new ProductTicket(7, false);
            assertDoesNotThrow(() -> t.close()); // sin productos, debe cerrar
			
			assertFalse(t.isOpen());
			assertNotNull(t.getDateClosed());
			assertEquals(closedComposedId(7, false), t.getComposedId());
        }
		
		
        @Test
			void composedId_whenClosedCustom_isIdThenCloseDate() {
            Ticket t = new ProductTicket(7, true);
            assertDoesNotThrow(() -> t.close()); // sin productos, debe cerrar
			
			assertFalse(t.isOpen());
			assertNotNull(t.getDateClosed());
			assertEquals(closedComposedId(7, true), t.getComposedId());
        }
    }
	
    @Nested
		class AddProduct_BaseProduct {
		
        @Test
			void addBaseProduct_firstTime_discountAppearsForClothesWhenAmountGt1() throws Exception {
            Ticket t = new ProductTicket(1, true);
            // CLOTHES: descuento 7%
            BaseProduct p = new BaseProduct(10, "Alpha", 10.0, "CLOTHES", 0, false);
			
            t.addItem(p, 2, new String[0]); // 2 unidades => aplica descuento de categoría
			
            String s = t.summaryString();
            assertTrue(s.contains("Ticket : " + openComposedId(1, true)), s);
            assertTrue(s.contains("**discount -"), s+"\nRESULT: En CLOTHES con 2 unidades debe aparecer descuento");
            // descuento: 7% de 10.0 = 0.7 por item; por 2 => 1.4
            assertTrue(s.contains("  Total price: 20.0"), s+"\nRESULT: Precio total deberia ser 20");
            assertTrue(s.contains("  Total discount: 1.4"), s+"\nRESULT: Discount total deberia ser 1.4");
            assertTrue(s.contains("  Final Price: 18.6"), s+"\nRESULT: Precio final deberia ser 18.6");
        }
		
        @Test
			void addBaseProduct_duplicateSamePersonalizations_incrementsAmount() throws Exception {
            Ticket t = new ProductTicket(1, true);
            BaseProduct p = new BaseProduct(10, "Alpha", 10.0, "CLOTHES", 0, false);
			
            t.addItem(p, 1, new String[0]);
            t.addItem(p, 1, new String[0]);
			
            String s = t.summaryString();
            assertTrue(s.contains("  Total price: 20.0"), "2 unidades * 10.0 = 20.0");
        }
		
        @Test
			void addBaseProduct_newProduct_overflow_throwsFullCollectionException() throws Exception {
            Ticket t = new ProductTicket(1, true);
            BaseProduct p1 = new BaseProduct(10, "Alpha", 1.0, "CLOTHES", 0, false);
            BaseProduct p2 = new BaseProduct(11, "Beta", 1.0, "CLOTHES", 0, false);
			
            t.addItem(p1, Ticket.MAX_PRODUCTS, new String[0]); // llena totalAmount=100
			
            assertThrows(FullCollectionException.class, () -> t.addItem(p2, 1, new String[0]));
        }
		
        @Test
			void addBaseProduct_duplicateOverflow_doesNothing_noException() throws Exception {
            Ticket t = new ProductTicket(1, true);
            BaseProduct p1 = new BaseProduct(10, "Alpha", 1.0, "CLOTHES", 0, false);
			
            t.addItem(p1, 99, new String[0]);
			
            // duplicado: si se pasa de MAX, tu código NO lanza, simplemente no incrementa
			assertThrows(FullCollectionException.class, () -> t.addItem(p1, 2, new String[0]));
			
            String s = t.summaryString();
            assertTrue(s.contains("  Total price: 99.0"), s+"\nRESULT: Debe quedarse en 99.0, no subir a 101.0");
        }
		
        @Test
			void addBaseProduct_sameIdDifferentPersonalizations_countsAsDifferentItems() throws Exception {
            Ticket t = new ProductTicket(1, true);
            // CLOTHES permite personalizaciones (máx. 5)
            BaseProduct p = new BaseProduct(10, "Alpha", 10.0, "CLOTHES", 5, true);
			
            t.addItem(p, 1, new String[]{"A"});
            t.addItem(p, 1, new String[]{"B", "C"}); // distinta personalización => no es duplicado
			
            String s = t.summaryString();
            assertTrue(s.contains("personalizationList:[A]"), s+"\nRESULT: Deberia tener personalizacion A ");
            assertTrue(s.contains("personalizationList:[B, C]"), s+"\nRESULT: Deberia tener personalizaciones B, C ");
			
            // Precio efectivo: 10*(1+0.1*1)=11 y 10*(1+0.1*2)=12 => total 23.0
            assertTrue(s.contains("  Total price: 23.0"), s+"\nRESULT: Precio total deberia ser 23.0 ");
        }
		
        @Test
			void addBaseProduct_personalization_affectsPriceAndDiscount() throws Exception {
            Ticket t = new ProductTicket(1, true);
            BaseProduct p = new BaseProduct(10, "Alpha", 10.0, "CLOTHES", 5, true);
			
            t.addItem(p, 2, new String[]{"X", "Y"}); // 2 pers => +20% => 12.0 cada uno
			
            String s = t.summaryString();
            // total price = 12.0*2 = 24.0
            assertTrue(s.contains("  Total price: 24.0"), s+"\nRESULT: Precio total deberia ser 24.0");
            // descuento CLOTHES 7%: 0.84 por item => 1.68
            assertTrue(s.contains("  Total discount: 1.68"), s+"\nRESULT: Descuento total deberia ser 1.68");
            assertTrue(s.contains("  Final Price: 22.32"), s+"\nRESULT: Precio final deberia ser 22.32");
        }
    }
	
    @Nested
		class AddProduct_TimedProduct {
		
        @Test
			void addTimedProduct_amountOverMaxParticipants_notAdded_ticketStaysEmpty() throws Exception {
            Ticket t = new ProductTicket(1, true);
            TimedProduct tp = new TimedProduct(20, "Event", 50.0, 3, "MEETING", App.now().plusDays(10));
			
			// 4 > maxParticipants(3) => no añade nada
			assertThrows(InvalidDataException.class, () -> t.addItem(tp, 4, new String[0]));
            assertTrue(t.isEmpty());
        }
		
        @Test
			void addTimedProduct_duplicate_throwsDuplicateItemException() throws Exception {
            Ticket t = new ProductTicket(1, true);
            TimedProduct tp = new TimedProduct(20, "Event", 50.0, 10, "MEETING", App.now().plusDays(10));
			
            t.addItem(tp, 2, new String[0]);
            assertThrows(DuplicateItemException.class, () -> t.addItem(tp, 1, new String[0]));
        }
		
        @Test
			void addTimedProduct_countsAsOneTowardMaxProducts() throws Exception {
            Ticket t = new ProductTicket(1, true);
			
            for (int i = 0; i < Ticket.MAX_PRODUCTS; i++) {
                TimedProduct tp = new TimedProduct(1000 + i, "E" + i, 1.0, 100, "MEETING", App.now().plusDays(30));
                t.addItem(tp, 10, new String[0]); // cuenta como 1 en totalAmount
            }
			
            TimedProduct extra = new TimedProduct(9999, "EXTRA", 1.0, 100, "MEETING", App.now().plusDays(30));
            assertThrows(FullCollectionException.class, () -> t.addItem(extra, 1, new String[0]));
        }
    }
	
    @Nested
		class RemoveProduct {
		
        @Test
			void removeProduct_existing_returnsTrue_andEmptiesTicket() throws Exception {
            Ticket t = new ProductTicket(1, true);
            BaseProduct p = new BaseProduct(10, "Alpha", 10.0, "CLOTHES", 0, false);
			
            t.addItem(p, 1, new String[0]);
			
			assertDoesNotThrow(() -> t.removeItem(p));
            assertTrue(t.isEmpty());
        }
		
        @Test
			void removeProduct_nonExisting_returnsFalse() throws Exception {
            Ticket t = new ProductTicket(1, true);
			BaseProduct p = new BaseProduct(10, "Alpha", 10.0, "CLOTHES", 0, false);
			assertThrows(MissingItemException.class, () -> t.removeItem(p));
        }
		
        @Test
			void removeProduct_freesCapacity_new_BaseProduct() throws Exception {
            Ticket t = new ProductTicket(1, true);
            BaseProduct p1 = new BaseProduct(10, "Alpha", 1.0, "CLOTHES", 0, false);
            BaseProduct p2 = new BaseProduct(11, "Beta", 1.0, "CLOTHES", 0, false);
			
            t.addItem(p1, 100, new String[0]);
            assertThrows(FullCollectionException.class, () -> t.addItem(p2, 1, new String[0]));
			
			assertDoesNotThrow(() -> t.removeItem(p1));
            assertDoesNotThrow(() -> t.addItem(p2, 100, new String[0]));
        }
		
		
		@Test
			void removeItem_ServiceWhenRemovingProductSameIdNumber() throws Exception {
			Ticket t = new CombinedTicket(1, true);
			
            ServiceProduct s = new ServiceProduct(1, "INSURANCE", App.now().plusHours(1));
			assertDoesNotThrow(() -> t.addItem(s, 1, new String[0]));
			
			BaseProduct p = new BaseProduct(1, "Alpha", 1.0, "CLOTHES", 0, false);
			assertThrows(MissingItemException.class, () -> t.removeItem(p));
			
			assertTrue(t.summaryString().contains("INSURANCE")); // NOTE(erb): wasn't deleted
		}
		
		
		@Test
			void removeItem_ProductWhenRemovingServiceSameIdNumber() throws Exception {
			Ticket t = new CombinedTicket(1, true);
			
			BaseProduct p = new BaseProduct(1, "Alpha", 1.0, "CLOTHES", 0, false);
			assertDoesNotThrow(() -> t.addItem(p, 1, new String[0]));
			
            ServiceProduct s = new ServiceProduct(1, "INSURANCE", App.now().plusHours(1));
			assertThrows(MissingItemException.class, () -> t.removeItem(s));
			
			assertTrue(t.summaryString().contains("Alpha")); // NOTE(erb): wasn't deleted
		}
		
	}
	
	@Nested
		class TryClose {
		
		@Test
			void close_withExpiredTimedProduct_throws_andKeepsOpen() throws Exception {
			Ticket t = new ProductTicket(1, true);
			
			TimedProduct.TimedType type = TimedProduct.TimedType.MEETING;
			
			assertDoesNotThrow(() -> {
								   LocalDateTime expirationDate = App.now().plusHours(type.hoursForPreparing + 1);
								   TimedProduct expired = new TimedProduct(20, "Expired", 10.0, 10, type, expirationDate);
								   t.addItem(expired, 1, new String[0]);
							   });
			
			// Expira en el pasado; además getExpirationDate resta horas, así que sigue siendo pasado.
			AppTest.setAppTime(App.now().plusDays(type.hoursForPreparing * 2));
			
			assertThrows(InvalidDataException.class, t::close);
			assertTrue(t.isOpen());
		}
		
		@Test
			void close_ok_closesAndSetsDateClosed() throws Exception {
			final Ticket t = new ProductTicket(1, true);
			TimedProduct ok = new TimedProduct(20, "Ok", 10.0, 10, TimedProduct.TimedType.MEETING, App.now().plusDays(30));
			
			assertDoesNotThrow(() -> {
								   t.addItem(ok, 1, new String[0]);
								   t.close();
							   });
			
			assertFalse(t.isOpen());
			assertNotNull(t.getDateClosed());
			assertEquals(closedComposedId(1), t.getComposedId());
		}
		
		@Test
			void close_makesDefensiveCopy_productsNotAffectedByExternalMutation() throws Exception {
			final Ticket t = new ProductTicket(1, true);
			final BaseProduct p = new BaseProduct(10, "Alpha", 10.0, "CLOTHES", 5, true);
			final String[] pers = new String[]{"X"};
			
			
			assertDoesNotThrow(() -> {
								   t.addItem(p, 1, pers);
								   t.close(); // clona productos y personalizaciones
							   });
			
			// mutaciones externas
			p.setName("MUTATED");
			
			String s = t.summaryString();
			assertTrue(s.contains("name:'Alpha'"), s+"\nRESULT: El ticket debería conservar el nombre original tras cerrar");
		}
		
		
		@Test
			void close_ok_emptyServiceTicket() throws Exception {
			final Ticket t = new ServiceTicket(1, true);
			
			assertDoesNotThrow(() -> t.close()); // clona productos y personalizaciones
			
		}
		
		@Test
			void close_combinedWithoutProduct_throws_andKeepsOpen() throws Exception {
			final Ticket t = new CombinedTicket(1, true);
			
			final ServiceProduct service = new ServiceProduct(20, "INSURANCE", App.now().plusHours(1));
			
			assertDoesNotThrow(() -> t.addItem(service, 1, new String[0]));
			
			assertThrows(InvalidDataException.class, t::close);
			assertTrue(t.isOpen());
		}
		
		@Test
			void close_combinedWithoutService_throws_andKeepsOpen() throws Exception {
			final Ticket t = new CombinedTicket(1, true);
			
			final BaseProduct p = new BaseProduct(10, "Alpha", 10.0, "CLOTHES", 5, true);
			
			assertDoesNotThrow(() -> t.addItem(p, 1, new String[0]));
			
			assertThrows(InvalidDataException.class, t::close);
			assertTrue(t.isOpen());
		}
		
		
		@Test
			void close_ok_combined() throws Exception {
			final Ticket t = new CombinedTicket(1, true);
			
			final BaseProduct p = new BaseProduct(10, "Alpha", 10.0, "CLOTHES", 5, true);
			final ServiceProduct service = new ServiceProduct(20, "INSURANCE", App.now().plusHours(1));
			
			assertDoesNotThrow(() -> t.addItem(service, 1, new String[0]));
			assertDoesNotThrow(() -> t.addItem(p, 1, new String[0]));
			
			assertDoesNotThrow(t::close);
			assertFalse(t.isOpen());
		}
	}
	
}
