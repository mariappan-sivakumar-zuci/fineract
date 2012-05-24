package org.mifosng.platform.api;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Locale;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mifosng.data.EntityIdentifier;
import org.mifosng.data.LoanProductData;
import org.mifosng.data.LoanProductList;
import org.mifosng.data.command.LoanProductCommand;
import org.mifosng.platform.api.infrastructure.ApiDataConversionService;
import org.mifosng.platform.loanproduct.service.LoanProductReadPlatformService;
import org.mifosng.platform.loanproduct.service.LoanProductWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/v1/loanproducts")
@Component
@Scope("singleton")
public class LoanProductApiResource {

    @Autowired
	private LoanProductReadPlatformService loanProductReadPlatformService;

	@Autowired
	private LoanProductWritePlatformService loanProductWritePlatformService;
	
	@Autowired
	private ApiDataConversionService apiDataConversionService;
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON})
	public Response createLoanProduct(LoanProductCommand command) {

		EntityIdentifier entityIdentifier = this.loanProductWritePlatformService.createLoanProduct(command);

		return Response.ok().entity(entityIdentifier).build();
	}

    @GET
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({MediaType.APPLICATION_JSON})
	public Response retrieveAllLoanProducts() {
    	
		Collection<LoanProductData> products = this.loanProductReadPlatformService.retrieveAllLoanProducts();

		return Response.ok().entity(new LoanProductList(products)).build();
    }
    
    /*
	 * see http://stackoverflow.com/questions/5250074/what-uri-can-be-used-to-request-a-default-resource
	 */
	@GET
	@Path("template")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON})
	public Response retrieveNewLoanProductDetails() {

		LoanProductData loanProduct = this.loanProductReadPlatformService.retrieveNewLoanProductDetails();

		return Response.ok().entity(loanProduct).build();
	}
    
	@GET
	@Path("{productId}")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON})
	public Response retrieveLoanProductDetails(@PathParam("productId") final Long productId) {

		LoanProductData loanProduct = this.loanProductReadPlatformService.retrieveLoanProduct(productId);

		return Response.ok().entity(loanProduct).build();
	}

	@PUT
	@Path("{productId}")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response updateLoanProduct(@PathParam("productId") final Long productId, final LoanProductCommand command) {
		
		// FIXME - pass in locale through query string or in 'request body'
		Locale clientApplicationLocale = Locale.UK;
		BigDecimal principal = this.apiDataConversionService.convertFrom(command.getPrincipalFormatted(), "principalFormatted", clientApplicationLocale);
		BigDecimal interestRatePerPeriod = this.apiDataConversionService.convertFrom(command.getInterestRatePerPeriodFormatted(), "interestRatePerPeriodFormatted", clientApplicationLocale);
		BigDecimal inArrearsToleranceAmount = this.apiDataConversionService.convertFrom(command.getInArrearsToleranceAmountFormatted(), "inArrearsToleranceAmountFormatted", clientApplicationLocale);
		command.setPrincipal(principal);
		command.setInterestRatePerPeriod(interestRatePerPeriod);
		command.setInArrearsToleranceAmount(inArrearsToleranceAmount);

		command.setId(productId);
		EntityIdentifier entityIdentifier = this.loanProductWritePlatformService.updateLoanProduct(command);
		
		return Response.ok().entity(entityIdentifier).build();
	}
}