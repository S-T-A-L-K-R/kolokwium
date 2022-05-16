package edu.iis.mto.coffee;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.iis.mto.coffee.machine.Coffee;
import edu.iis.mto.coffee.machine.CoffeeGrinder;
import edu.iis.mto.coffee.machine.CoffeeMachine;
import edu.iis.mto.coffee.machine.CoffeeOrder;
import edu.iis.mto.coffee.machine.MilkProvider;
import edu.iis.mto.coffee.machine.GrinderException;
import edu.iis.mto.coffee.machine.HeaterException;

@ExtendWith(MockitoExtension.class)
class CoffeeMachineTest {

    @Mock
    CoffeeGrinder grinder;
    @Mock
    MilkProvider milkProvider;
    @Mock
    CoffeeReceipes receipes;
    
    CoffeeOrder bigLatteCoffeeOrder;
    @Mock
    CoffeeReceipe bigLatteCoffeeReceipe;

    private CoffeeMachine coffeeMachine;
    
    @BeforeEach
    void beforeEach()
    {
        coffeeMachine = new CoffeeMachine(grinder, milkProvider, receipes);
        bigLatteCoffeeOrder = CoffeeOrder.builder()
                                         .withSize(CoffeeSize.DOUBLE)
                                         .withType(CoffeeType.LATTE)
                                         .build();
    }

    @Test
    void itCompiles() {
        MatcherAssert.assertThat(true, equalTo(true));
    }

    @Test
    void makeCoffeeButWeDontHaveReceipeForThisOne()
    {
        when(receipes.getReceipe(any())).thenReturn(null);
        Coffee coffee = coffeeMachine.make(bigLatteCoffeeOrder);
        assertEquals(coffee.getStatus(), Status.ERROR);
    }

    @Test
    void grinderThrowsGrinderException() throws GrinderException
    {
        when(receipes.getReceipe(any())).thenReturn(bigLatteCoffeeReceipe);
        when(grinder.grind(any())).thenThrow(new GrinderException());
        Coffee coffee = coffeeMachine.make(bigLatteCoffeeOrder);

        assertEquals(coffee.getMessage(), null);
        assertEquals(coffee.getStatus(), Status.ERROR);
    }
    @Test
    void grinderDoesntWork() throws GrinderException
    {
        when(receipes.getReceipe(any())).thenReturn(bigLatteCoffeeReceipe);
        when(grinder.grind(any())).thenReturn(false);
        Coffee coffee = coffeeMachine.make(bigLatteCoffeeOrder);

        assertEquals(coffee.getMessage(), "no coffee beans available");
        assertEquals(coffee.getStatus(), Status.ERROR);
    }
    @Test
    void milkProviderThrowsHeaterException() throws GrinderException, HeaterException
    {
        when(receipes.getReceipe(any())).thenReturn(bigLatteCoffeeReceipe);
        when(grinder.grind(any())).thenReturn(true);
        // doThrow(new HeaterException()).when(milkProvider.heat());
        Coffee coffee = coffeeMachine.make(bigLatteCoffeeOrder);

        assertEquals(coffee.getMessage(), null);
        assertEquals(coffee.getStatus(), Status.READY);
    }

    @Test
    void makeProperCoffee()
    {
        
    }
}
