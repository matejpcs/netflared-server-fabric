package com.matejpcs.netflared;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import java.nio.file.Path;
public final class NetflaredFabric implements ModInitializer {
 private NetflaredCore core; private MinecraftServer server;
 @Override public void onInitialize() {
  CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(Commands.literal("netflared")
   .requires(source -> source.hasPermission(4))
   .executes(context->{run(context.getSource(),new String[0]);return 1;})
   .then(Commands.literal("status").executes(context->{run(context.getSource(),new String[]{"status"});return 1;}))
   .then(Commands.literal("info").executes(context->{run(context.getSource(),new String[]{"info"});return 1;}))
   .then(Commands.literal("connect").executes(context->{run(context.getSource(),new String[]{"connect"});return 1;}))
   .then(Commands.literal("disconnect").executes(context->{run(context.getSource(),new String[]{"disconnect"});return 1;}))
   .then(Commands.literal("reset").executes(context->{run(context.getSource(),new String[]{"reset"});return 1;}))
   .then(Commands.literal("setup").then(Commands.argument("args",StringArgumentType.greedyString()).executes(context->{run(context.getSource(),new String[]{"setup",StringArgumentType.getString(context,"args")});return 1;})))));
  ServerLifecycleEvents.SERVER_STARTED.register(server->{this.server=server;try{core=new NetflaredCore(Path.of("config","netflared"),()->new NetflaredCore.ServerInfo(server.getPort(),"fabric"),server::execute);core.autoConnect();}catch(Exception e){server.sendSystemMessage(Component.literal("Netflared failed to initialize: "+e.getMessage()));}});
  ServerLifecycleEvents.SERVER_STOPPING.register(server->{if(core!=null)core.shutdown();});
 }
 private void run(CommandSourceStack source,String[] args){if(core==null){source.sendFailure(Component.literal("Netflared is not initialized."));return;}core.handle(new NetflaredCore.Sender(){public boolean admin(){return true;}public void send(String message){source.sendSuccess(()->Component.literal(message.replaceAll("§.","")),false);}},args);}
}