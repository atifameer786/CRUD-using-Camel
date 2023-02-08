package sample.camel.route;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.DefaultMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.apache.camel.model.rest.RestBindingMode;
import sample.camel.component.ExchangeJpa;
import sample.camel.entity.Employee;
import javax.annotation.Resource;

@Component
public class RestRoute extends RouteBuilder {

//    @Resource
    @Autowired
    ExchangeJpa exchangeJpa;

    @Override
    public void configure()  {

        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .enableCORS(true);

        //TEST HELLO WORLD API
        from("direct:hello").transform().constant("Hello-World");
        rest("/api")

                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .get("/test").to("direct:hello");

        //FIND ALL EMPLOYEE API
        rest("/api")
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .get("/findall").to("direct:show")
                .route()
                .bean(ExchangeJpa.class, "findAllEmployee")
                .endRest();

        //FIND EMPLOYEE BY ID
        rest("/api")
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .get("/find/{id}").route()
                .bean(ExchangeJpa.class, "findEmployee(${header.id})");


        //CREATE EMPLOYEE API
        from("direct:saveData").process(this::saveEmployeeAndSetToExchange);
        rest("/api")
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .post("/create")
                .type(Employee.class)
                .to("direct:saveData");


        //DELETE EMPLOYEE API
        from("direct:deleteEmployee").process(this::deleteEmployeeAndSetToExchange);
        rest("/api")
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .post("/delete/{id}")
                .type(Employee.class)
                .to("direct:deleteEmployee");


        //UPDATE EMPLOYEE
        from("direct:updateEmployee").process(this::updateEmployeeAndSetToExchange);
        rest("/api")
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .post("/update/{id}")
                .type(Employee.class)
                .to("direct:updateEmployee");


        // CONSUMERS
//        from("direct:hello").transform().constant("Hello-World");
//        from("direct:saveData").process(this::saveEmployeeAndSetToExchange);
//        from("direct:deleteEmployee").process(this::deleteEmployeeAndSetToExchange);
//        from("direct:updateEmployee").process(this::updateEmployeeAndSetToExchange);
    }


    //DELETE EXCHANGE
    private void deleteEmployeeAndSetToExchange(Exchange exchange) {
        Long empId = exchange.getMessage().getHeader("id", Long.class);
        Message message = new DefaultMessage(exchange.getContext());
        String deleteResponse = exchangeJpa.deleteEmployee(empId);
        message.setBody(deleteResponse);
        exchange.setMessage(message);


    }

    //UPDATE EXCHANGE
    private void updateEmployeeAndSetToExchange(Exchange exchange) {
        Long empId = exchange.getMessage().getHeader("id", Long.class);
        Employee empContent = exchange.getMessage().getBody(Employee.class);
        String updateResponse = exchangeJpa.updateEmployee(empId, empContent.getName(),
                                empContent.getL_name(), empContent.getMob_num(),
                                empContent.getEmail_id(), empContent.getJob_pos());
        Message message = new DefaultMessage(exchange.getContext());
        message.setBody(updateResponse);

    }

    // CREATE EXCHANGE
    private void saveEmployeeAndSetToExchange(Exchange exchange) {
        Employee emp = exchange.getMessage().getBody(Employee.class);
        Long str = exchangeJpa.createEmployee(emp.getName(), emp.getL_name(), emp.getMob_num()
                , emp.getEmail_id(), emp.getJob_pos());
        Message message = new DefaultMessage(exchange.getContext());
        message.setBody("Employee Created with id: "+ str);
        exchange.setMessage(message);
    }


}
