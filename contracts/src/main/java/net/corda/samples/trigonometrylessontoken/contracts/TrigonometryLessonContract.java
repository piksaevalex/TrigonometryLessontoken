package net.corda.samples.trigonometrylessontoken.contracts;

import com.r3.corda.lib.tokens.contracts.EvolvableTokenContract;
import net.corda.samples.trigonometrylessontoken.states.TrigonometryLessonState;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import static net.corda.core.contracts.ContractsDSL.requireThat;

/*
*  TrigonometryLessonContract governs the evolution of TrigonometryLessonState token. Evolvable tokens must extend the EvolvableTokenContract abstract class, it defines the
*  additionalCreateChecks and additionalCreateChecks method to add custom logic to validate while creation and update of evolvable tokens respectively.
* */
public class TrigonometryLessonContract extends EvolvableTokenContract implements Contract {

    public static final String CONTRACT_ID = "net.corda.samples.trigonometrylessontoken.contracts.TrigonometryLessonContract";

    @Override
    public void additionalCreateChecks(@NotNull LedgerTransaction tx) {
        // Write contract validation logic to be performed while creation of token
        TrigonometryLessonState outputState = (TrigonometryLessonState) tx.getOutput(0);
        requireThat( require -> {
            require.using("Valuation cannot be zero",
                    outputState.getPrice().getQuantity() >= 0);
            return null;
        });
    }

    @Override
    public void additionalUpdateChecks(@NotNull LedgerTransaction tx) {
        // Write contract validation logic to be performed while updation of token
    }

}
