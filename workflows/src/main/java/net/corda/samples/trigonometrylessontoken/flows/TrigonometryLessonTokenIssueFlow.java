package net.corda.samples.trigonometrylessontoken.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.tokens.workflows.utilities.NonFungibleTokenBuilder;
import net.corda.core.identity.CordaX500Name;
import com.google.common.collect.ImmutableList;
import com.r3.corda.lib.tokens.contracts.states.NonFungibleToken;
import com.r3.corda.lib.tokens.workflows.flows.rpc.CreateEvolvableTokens;
import com.r3.corda.lib.tokens.workflows.flows.rpc.IssueTokens;
import net.corda.core.contracts.Amount;
import net.corda.core.contracts.TransactionState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.samples.trigonometrylessontoken.states.TrigonometryLessonState;

import java.util.Currency;
import java.util.UUID;

@StartableByRPC
public class TrigonometryLessonTokenIssueFlow extends FlowLogic<String> {

    private final Party owner;
    private final Amount<Currency> price;
    private final int numberOfLessons;

    public TrigonometryLessonTokenIssueFlow(Party owner, Amount<Currency> price,
                                            int numberOfLessons) {
        this.owner = owner;
        this.price = price;
        this.numberOfLessons = numberOfLessons;
    }

    @Override
    @Suspendable
    public String call() throws FlowException {

        /** Explicit selection of notary by CordaX500Name - argument can by coded in flows or parsed from config (Preferred)*/
        final Party notary = getServiceHub().getNetworkMapCache().getNotary(CordaX500Name.parse("O=Notary,L=London,C=GB"));

        Party issuer = getOurIdentity();

        UniqueIdentifier uuid = UniqueIdentifier.Companion.fromString(UUID.randomUUID().toString());
        final TrigonometryLessonState trigonometryLessonState = new TrigonometryLessonState(uuid,
                ImmutableList.of(issuer), price, numberOfLessons);

        TransactionState<TrigonometryLessonState> transactionState = new TransactionState<>(trigonometryLessonState, notary);

        subFlow(new CreateEvolvableTokens(transactionState));

        NonFungibleToken TrigonometryLessonToken = new NonFungibleTokenBuilder()
                .ofTokenType(trigonometryLessonState.toPointer())
                .issuedBy(issuer)
                .heldBy(owner)
                .buildNonFungibleToken();

        SignedTransaction stx = subFlow(new IssueTokens(ImmutableList.of(TrigonometryLessonToken)));
        return "\nThe non-fungible TrigonometryLesson token is created with UUID: "+ uuid +". (This is what you will use in next step)"
                +"\nTransaction ID: "+stx.getId();

    }
}
