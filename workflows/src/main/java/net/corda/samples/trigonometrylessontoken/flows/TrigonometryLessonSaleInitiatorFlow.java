package net.corda.samples.trigonometrylessontoken.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.tokens.workflows.flows.move.MoveTokensUtilities;
import net.corda.core.identity.CordaX500Name;
import net.corda.samples.trigonometrylessontoken.states.TrigonometryLessonState;
import com.google.common.collect.ImmutableList;
import com.r3.corda.lib.tokens.contracts.states.FungibleToken;
import com.r3.corda.lib.tokens.workflows.internal.flows.distribution.UpdateDistributionListFlow;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.util.List;
import java.util.UUID;

@InitiatingFlow
@StartableByRPC
public class TrigonometryLessonSaleInitiatorFlow extends FlowLogic<String> {

    private final String TrigonometryLessonId;
    private final Party buyer;

    public TrigonometryLessonSaleInitiatorFlow(String TrigonometryLessonId, Party buyer) {
        this.TrigonometryLessonId = TrigonometryLessonId;
        this.buyer = buyer;
    }

    @Override
    @Suspendable
    public String call() throws FlowException {

        /** Explicit selection of notary by CordaX500Name - argument can by coded in flows or parsed from config (Preferred)*/
        final Party notary = getServiceHub().getNetworkMapCache().getNotary(CordaX500Name.parse("O=Notary,L=London,C=GB"));
        UUID uuid = UUID.fromString(TrigonometryLessonId);

        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(
                null, ImmutableList.of(uuid), null, Vault.StateStatus.UNCONSUMED);
        StateAndRef<TrigonometryLessonState> TrigonometryLessonStateAndRef = getServiceHub().getVaultService().
                queryBy(TrigonometryLessonState.class, queryCriteria).getStates().get(0);
        TrigonometryLessonState trigonometryLessonState = TrigonometryLessonStateAndRef.getState().getData();
        TransactionBuilder txBuilder = new TransactionBuilder(notary);
        MoveTokensUtilities.addMoveNonFungibleTokens(txBuilder, getServiceHub(), trigonometryLessonState.toPointer(), buyer);

        FlowSession buyerSession = initiateFlow(buyer);
        buyerSession.send(trigonometryLessonState.getPrice());

        List<StateAndRef<FungibleToken>> inputs = subFlow(new ReceiveStateAndRefFlow<>(buyerSession));
        List<FungibleToken> moneyReceived = buyerSession.receive(List.class).unwrap(value -> value);
        MoveTokensUtilities.addMoveTokens(txBuilder, inputs, moneyReceived);

        SignedTransaction initialSignedTrnx = getServiceHub().signInitialTransaction(txBuilder, getOurIdentity().getOwningKey());
        SignedTransaction signedTransaction = subFlow(new CollectSignaturesFlow(initialSignedTrnx, ImmutableList.of(buyerSession)));
        SignedTransaction stx = subFlow(new FinalityFlow(signedTransaction, ImmutableList.of(buyerSession)));

        subFlow(new UpdateDistributionListFlow(stx));

        return "\nThe Trigonometry Lesson is sold to "+ this.buyer.getName().getOrganisation() + "\nTransaction ID: "
                + stx.getId();
    }
}
