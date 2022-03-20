package net.corda.samples.trigonometrylessontoken.states;

import net.corda.samples.trigonometrylessontoken.contracts.TrigonometryLessonContract;
import com.google.common.collect.ImmutableList;
import com.r3.corda.lib.tokens.contracts.states.EvolvableTokenType;
import com.r3.corda.lib.tokens.contracts.types.TokenPointer;
import net.corda.core.contracts.Amount;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearPointer;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.util.Currency;
import java.util.List;

@BelongsToContract(TrigonometryLessonContract.class)
public class TrigonometryLessonState extends EvolvableTokenType {

    private final UniqueIdentifier linearId;
    private final List<Party> maintainers;
    private final Party issuer;
    private final int fractionDigits = 0;

    //Properties of Rub State. Some of these values may evolve over time.
    private final Amount<Currency> price;
    private final int numberOfLessons;

    public TrigonometryLessonState(UniqueIdentifier linearId, List<Party> maintainers, Amount<Currency> price, int numberOfLessons) {
        this.linearId = linearId;
        this.maintainers = maintainers;
        this.price = price;
        this.numberOfLessons = numberOfLessons;
        issuer = maintainers.get(0);
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    public int getNumberOfLessons() {
        return numberOfLessons;
    }

    public Amount<Currency> getPrice() {
        return price;
    }

    public Party getIssuer() {
        return issuer;
    }

    @Override
    public int getFractionDigits() {
        return fractionDigits;
    }

    @NotNull
    @Override
    public List<Party> getMaintainers() {
        return ImmutableList.copyOf(maintainers);
    }

    /* This method returns a TokenPointer by using the linear Id of the evolvable state */
    public TokenPointer<TrigonometryLessonState> toPointer(){
        LinearPointer<TrigonometryLessonState> linearPointer = new LinearPointer<>(linearId, TrigonometryLessonState.class);
        return new TokenPointer<>(linearPointer, fractionDigits);
    }
}