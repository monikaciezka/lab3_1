package pl.com.bottega.ecommerce.sales.domain.invoicing;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

}
