package net.foxdenstudio.foxcore.sponge.mixin.sponge.text.channel;


import net.foxdenstudio.foxcore.platform.text.Text;
import net.foxdenstudio.foxcore.platform.text.channel.MessageReceiver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(org.spongepowered.api.text.channel.MessageReceiver.class)
public interface MixinMessageReceiver extends MessageReceiver {

    @Shadow
    void sendMessage(org.spongepowered.api.text.Text message);

    @Override
    default void sendMessage(Text message) {
        if (message instanceof org.spongepowered.api.text.Text) {
            this.sendMessage((org.spongepowered.api.text.Text) message);
        } else {
            this.sendMessage(org.spongepowered.api.text.Text.of(message.toPlain()));
        }
    }

}
