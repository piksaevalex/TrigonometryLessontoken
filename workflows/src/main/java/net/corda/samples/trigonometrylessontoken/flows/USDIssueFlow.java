package net.corda.samples.trigonometrylessontoken.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.r3.corda.lib.tokens.contracts.states.FungibleToken;
import com.r3.corda.lib.tokens.money.MoneyUtilities;
import com.r3.corda.lib.tokens.workflows.flows.rpc.IssueTokens;
import com.r3.corda.lib.tokens.workflows.utilities.FungibleTokenBuilder;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;

@StartableByRPC
public class USDIssueFlow extends FlowLogic<SignedTransaction> {

    private final Long amount;
    private final Party recipient;

    public USDIssueFlow(Long amount, Party recipient) {
        this.amount = amount;
        this.recipient = recipient;

    }

    @Override
    @Suspendable
    public SignedTransaction call() throws FlowException {

        FungibleToken fungibleToken =
                new FungibleTokenBuilder()
                    .ofTokenType(MoneyUtilities.getUSD())
                    .withAmount(amount)
                    .issuedBy(getOurIdentity())
                    .heldBy(recipient)
                    .buildFungibleToken();

        return subFlow(new IssueTokens(ImmutableList.of(fungibleToken), ImmutableList.of(recipient)));
    }

}
