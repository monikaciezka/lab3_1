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
    private ClientData dummyClient;
    private InvoiceRequest invoiceRequest;
    private ProductData product;
    private Money productPrice;
    private Date sampleDate;
    private RequestItem sampleItem;
    private int quantity = 1;
    private Id sampleId;
    private Money taxValue;
    private ProductType productType = ProductType.FOOD;
    @Mock
    private TaxPolicy taxPolicyMock;


    @BeforeEach
    void setUp() throws Exception {
        invoiceFactory = new InvoiceFactory();
        bookKeeper = new BookKeeper(invoiceFactory);
        sampleId = Id.generate();
        dummyClient = new ClientData(sampleId, "Name");
        invoiceRequest = new InvoiceRequest(dummyClient);
        productPrice = new Money(500);
        sampleDate = new Date();
        taxValue = new Money(1);
        product = new ProductData(sampleId, productPrice, "sampleName", productType, sampleDate);
        sampleItem = new RequestItem(product, quantity, productPrice);

    }

    @Test
    void testSingleItemInvoiceRequest() {

        invoiceRequest.add(sampleItem);
        Tax tax = new Tax(taxValue, "taxes");
        when(taxPolicyMock.calculateTax(productType, productPrice)).thenReturn(tax);
        assertEquals(bookKeeper.issuance(invoiceRequest, taxPolicyMock).getItems().size(),1);
     }

     @Test
     void testTwoCalculateTaxCalls() {

        invoiceRequest.add(sampleItem);
        invoiceRequest.add(sampleItem);
        Tax tax = new Tax(taxValue, "taxes");
        when(taxPolicyMock.calculateTax(productType, productPrice)).thenReturn(tax);
        bookKeeper.issuance(invoiceRequest, taxPolicyMock);
        verify(taxPolicyMock, times(2)).calculateTax(productType, productPrice);
    }

     @Test
     void emptyInvoiceIssuanceTest() {

         assertEquals(bookKeeper.issuance(invoiceRequest, taxPolicyMock).getItems().size(), 0);
     }

     @Test
    void emptyInvoiceCalculateTaxTest() {

         bookKeeper.issuance(invoiceRequest, taxPolicyMock);
         verifyNoInteractions(taxPolicyMock);
     }

     @Test
    void twoItemsInvoiceTest() {

         invoiceRequest.add(sampleItem);
         invoiceRequest.add(sampleItem);
         Tax tax = new Tax(taxValue, "taxes");
         when(taxPolicyMock.calculateTax(productType, productPrice)).thenReturn(tax);

         assertEquals(bookKeeper.issuance(invoiceRequest, taxPolicyMock).getItems().size(),2);
     }

     @Test
    void threeCalculateTaxCallsTest() {

         invoiceRequest.add(sampleItem);
         invoiceRequest.add(sampleItem);
         invoiceRequest.add(sampleItem);
         Tax tax = new Tax(taxValue, "taxes");
         when(taxPolicyMock.calculateTax(productType, productPrice)).thenReturn(tax);
         bookKeeper.issuance(invoiceRequest, taxPolicyMock);
         verify(taxPolicyMock, times(3)).calculateTax(productType, productPrice);
     }
}
