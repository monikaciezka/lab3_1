package pl.com.bottega.ecommerce.sales.domain.invoicing;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.Date;

@ExtendWith(MockitoExtension.class)
class BookKeeperTest {

    private BookKeeper bookKeeper;
    private InvoiceFactory invoiceFactory;
    ClientData dummy;


    @Mock
    private TaxPolicy taxPolicyMock;


    @BeforeEach
    void setUp() throws Exception {
        invoiceFactory = new InvoiceFactory();
        bookKeeper = new BookKeeper(invoiceFactory);
        Id sampleId = Id.generate();
        dummy = new ClientData(sampleId, "Name");

    }

    @Test
    void testSingleItemInvoiceRequest() {

        InvoiceRequest invoiceRequest = new InvoiceRequest(dummy);
        ProductData productData = new ProductData(Id.generate(), new Money(100), "sampleName", ProductType.FOOD, new Date());
        RequestItem stub = new RequestItem(productData, 1, new Money(100));
        invoiceRequest.add(stub);
        Tax tax = new Tax(new Money(1), "taxes");
        when(taxPolicyMock.calculateTax(ProductType.FOOD, new Money(100))).thenReturn(tax);
        assertEquals(bookKeeper.issuance(invoiceRequest, taxPolicyMock).getItems().size(),1);
     }

     @Test
     void testTwoCalculateTaxCalls() {
         InvoiceRequest invoiceRequest = new InvoiceRequest(dummy);

         ProductData productData1 = new ProductData(Id.generate(), new Money(100), "sampleName", ProductType.FOOD, new Date());
         ProductData productData2 = new ProductData(Id.generate(), new Money(100), "sampleName", ProductType.FOOD, new Date());

         RequestItem stub1 = new RequestItem(productData1, 1, new Money(100));
         RequestItem stub2 = new RequestItem(productData2, 1, new Money(100));
         invoiceRequest.add(stub1);
         invoiceRequest.add(stub2);
         Tax tax = new Tax(new Money(1), "taxes");
         when(taxPolicyMock.calculateTax(ProductType.FOOD, new Money(100))).thenReturn(tax);
        bookKeeper.issuance(invoiceRequest, taxPolicyMock);
        verify(taxPolicyMock, times(2)).calculateTax(ProductType.FOOD, new Money(100));

    }

     @Test
     void emptyInvoiceIssuanceTest() {
         InvoiceRequest invoiceRequest = new InvoiceRequest(dummy);
         assertEquals(bookKeeper.issuance(invoiceRequest, taxPolicyMock).getItems().size(), 0);

     }

     @Test
    void emptyInvoiceCalculateTaxTest() {
         InvoiceRequest invoiceRequest = new InvoiceRequest(dummy);
         bookKeeper.issuance(invoiceRequest, taxPolicyMock);
         verifyNoInteractions(taxPolicyMock);
     }

     @Test
    void twoItemsInvoiceTest() {

         InvoiceRequest invoiceRequest = new InvoiceRequest(dummy);
         ProductData productData = new ProductData(Id.generate(), new Money(100), "sampleName", ProductType.FOOD, new Date());
         RequestItem stub = new RequestItem(productData, 100, new Money(100));
         invoiceRequest.add(stub);
         invoiceRequest.add(stub);
         Tax tax = new Tax(new Money(1), "taxes");
         when(taxPolicyMock.calculateTax(ProductType.FOOD, new Money(100))).thenReturn(tax);

         assertEquals(bookKeeper.issuance(invoiceRequest, taxPolicyMock).getItems().size(),2);

     }

     @Test
    void threeCalculateTaxCallsTest() {
         InvoiceRequest invoiceRequest = new InvoiceRequest(dummy);

         ProductData productData1 = new ProductData(Id.generate(), new Money(100), "sampleName", ProductType.FOOD, new Date());
         ProductData productData2 = new ProductData(Id.generate(), new Money(100), "sampleName", ProductType.FOOD, new Date());

         RequestItem stub1 = new RequestItem(productData1, 1, new Money(100));
         RequestItem stub2 = new RequestItem(productData2, 1, new Money(100));
         invoiceRequest.add(stub1);
         invoiceRequest.add(stub2);
         invoiceRequest.add(stub1);
         Tax tax = new Tax(new Money(1), "taxes");
         when(taxPolicyMock.calculateTax(ProductType.FOOD, new Money(100))).thenReturn(tax);
         bookKeeper.issuance(invoiceRequest, taxPolicyMock);
         verify(taxPolicyMock, times(3)).calculateTax(ProductType.FOOD, new Money(100));
     }
}
