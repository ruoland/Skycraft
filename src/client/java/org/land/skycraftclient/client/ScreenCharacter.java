package org.land.skycraftclient.client;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.AddServerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ScreenCharacter extends Screen {
    private static final Identifier CUSTOM_FONT = Identifier.of("skycraftclient", "custom_font");
    private TextFieldWidget widget;
    private static  final Style style = (Style.EMPTY.withFont(CUSTOM_FONT));
    protected ScreenCharacter(Text title) {
        super(title);
    }

    ButtonWidget choice2;

    ButtonWidget choice3;

    ButtonWidget choice1;

    @Override
    protected void init() {
        super.init();

        choice1 = new ButtonWidget.Builder(Text.literal("몰락한 귀족이었다.").setStyle(style), button -> {
            selectButton(1);

        }).dimensions((int)(this.width * 0.71), (int)(this.height * 0.35), 100, 20).build();
        addDrawableChild(choice1);


        choice2 = new ButtonWidget.Builder(Text.literal("여행 중인 상인이었다.").setStyle(style), button -> {
            selectButton(2);
        }).dimensions((int)(this.width * 0.71), (int)(this.height * 0.5), 100, 20).build();
        addDrawableChild(choice2);


        choice3 = new ButtonWidget.Builder(Text.literal("콜로세움의 전사였다.").setStyle(style), button -> {
            selectButton(3);
            System.out.println("버튼 누름");
        }).dimensions((int)(this.width * 0.71), (int)(this.height * 0.65), 100, 20).build();
        addDrawableChild(choice3);

        choice1.active = true;
        choice2.active = true;
        choice3.active = true;

        choice1.setAlpha(0.0F);
        choice2.setAlpha(0.0F);
        choice3.setAlpha(00F);
        nextPage(1);


        widget = new TextFieldWidget(textRenderer, (int) (width * 0.45), (int)(this.height * 0.35), 40, 20, Text.literal("이름").setStyle(style));
        widget.setDrawsBackground(false);
        widget.setPlaceholder(Text.literal("이름").setStyle(style));

        addSelectableChild(widget);

    }



    public void nextPage(int i){
        ButtonWidget continueButton = new ButtonWidget.Builder(Text.literal("계속하기").setStyle(style), button -> {
            nextPage(i+1);
        }).dimensions((int)(this.width * 0.05), (int)(this.height * 0.25), 100, 20).build();
        continueButton.setAlpha(0);
        addDrawableChild(continueButton);

        ButtonWidget backButton = new ButtonWidget.Builder(Text.literal("뒤로가기").setStyle(style), button -> {
            nextPage(i+1);
        }).dimensions((int)(this.width * 0.05), (int)(this.height * 0.65), 100, 20).build();
        backButton.setAlpha(0);
        addDrawableChild(backButton);

    }

    public void selectButton(int i){
        choice1.active = true;
        choice2.active = true;
        choice3.active = true;
        switch (i){
            case 1: choice1.active = false;
                break;
            case 2: choice2.active = false;
                break;
            case 3: choice3.active = false;
                break;
        }

    }

    public int calcWidth(double size){
        return (int) (width * size);
    }
    public int calcHeight(double size){
        return (int) (height * size);
    }
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

        context.fill((int) (width * 0.05),0, (int) (width * 0.3), height, 0x80000000);
        context.fill((int) (width * 0.7),0, (int) (width * 0.95), height, 0x80000000);

        // 제목 텍스트
        float titleScale = 1.0F;
        context.getMatrices().push();
        context.getMatrices().scale(titleScale, titleScale, titleScale);
        int titleWidth = 100;
        int titleX = (int) (calcWidth(0.85) / titleScale - titleWidth / 2);
        int titleY = (int) (calcHeight(0.1) / titleScale);
        context.drawTextWrapped(this.textRenderer, Text.literal("캐릭터 특성 선택 어쩌고저쩌고..").setStyle(style), titleX, titleY, titleWidth, 0xFFFFFFFF);
        context.getMatrices().pop();

// 설명 텍스트
        float descScale = 0.7F;
        context.getMatrices().push();
        context.getMatrices().scale(descScale, descScale, descScale);
        int descWidth = 100;
        int descX = (int) (calcWidth(0.85) / descScale - descWidth / 2);
        int descY = (int) (calcHeight(0.2) / descScale);
        context.drawTextWrapped(this.textRenderer, Text.literal("특성은 중요하다 어쩌고저쩌고..").setStyle(style), descX, descY, descWidth, 0xFFFFFFFF);
        context.getMatrices().pop();
        context.fill( widget.getX(), widget.getY(), widget.getX() + widget
                .getWidth(), widget.getY() + (widget.getHeight() / 2) , 0x80000000);

        widget.render(context, mouseX, mouseY, delta);




        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        //super.renderBackground(context, mouseX, mouseY, delta);
    }
}
