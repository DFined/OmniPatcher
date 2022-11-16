package com.dfined.omnipatcher.v2.ui;

import com.dfined.omnipatcher.application.gui.SelfRootedGUIScreen;
import com.dfined.omnipatcher.application.gui.VBoxWithSelection;
import com.dfined.omnipatcher.v2.DFUtil;
import com.dfined.omnipatcher.v2.Globals;
import com.dfined.omnipatcher.v2.OmniPatcherV2;
import com.dfined.omnipatcher.v2.error.ExceptionInfo;
import com.dfined.omnipatcher.v2.filesystem.ValveResourceManager;
import com.dfined.omnipatcher.v2.model.*;
import com.google.common.collect.Lists;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.NoArgsConstructor;
import one.util.streamex.StreamEx;
import org.controlsfx.control.CheckComboBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.dfined.omnipatcher.v2.filesystem.GamePathConstants.ICONS_PATH_PREFIX;


@NoArgsConstructor
public class OmniPatcherUi extends AnchorPane implements SelfRootedGUIScreen {
    private static final String ANY_SELECTOR_ELEMENT = "< Any >";
    private static final String FXML_ADDRESS = "omni_patcher_ui.fxml";
    private static final int DEFAULT_WIDTH = 1400;
    private static final int DEFAULT_HEIGHT = 700;
    private static final String WINDOW_NAME = "DFined's OmniPatcher V2";
    private static final String PAGE_INFO_PATTERN = "Page %d of %d";

    int page = 0;
    int pageCount = 0;

    List<List<WearableItem>> itemsResult;
    VBoxWithSelection<WearableItemResultTile> itemSearchResults;
    VBoxWithSelection<WearableItemResultTile> installQueue;

    @FXML
    TextField itemNameSelector;
    @FXML
    TextField pageSizeInput;
    @FXML
    ComboBox<String> heroSelector;
    @FXML
    ComboBox<String> slotSelector;
    @FXML
    CheckComboBox<String> raritiesSelector;
    @FXML
    Label pageInfoLabel;
    @FXML
    CheckBox workshopCheck;
    @FXML
    ProgressBar wearableSearchProgressBar;
    @FXML
    BorderPane loadingProgressOverlay;
    @FXML
    ScrollPane searchResScrl;
    @FXML
    ScrollPane installedScrlPane;
    @FXML
    Label detailsItemNameLabel;
    @FXML
    Label detailsHero;
    @FXML
    Label detailsSlot;
    @FXML
    ComboBox<String> detailsSelectHero;
    @FXML
    ComboBox<String> detailsSelectSlot;
    @FXML
    ImageView detailsImageView;

    @Override
    public String getName() {
        return WINDOW_NAME;
    }

    @Override
    public String getFXMLAddress() {
        return FXML_ADDRESS;
    }

    @Override
    public int getDefaultWidth() {
        return DEFAULT_WIDTH;
    }

    @Override
    public int getDefaultHeight() {
        return DEFAULT_HEIGHT;
    }

    @FXML
    public void onHeroSelected() {
        var hero = heroSelector.getSelectionModel().getSelectedItem();
        List<String> slots = new ArrayList<>();
        if (heroSelector.getSelectionModel().getSelectedIndex() > 0) {
            slots = DFUtil.convert(Globals.getHeroByQualifiedName(hero).getValidSlots().values(), WearableItemSlot::getName);
        }
        slots.add(0, ANY_SELECTOR_ELEMENT);
        slotSelector.setItems(FXCollections.observableList(slots));
        slotSelector.getSelectionModel().select(0);
    }

    @FXML
    public void onSearch() {
        setWearableProgress(0);
        var rarities = DFUtil.convert(raritiesSelector.getCheckModel().getCheckedItems(), ItemRarity::valueOf);
        var pageSize = pageSize();
        var items = Globals.getItems(getValueWithAny(heroSelector), getValueWithAny(slotSelector), itemNameSelector.getText(), rarities, workshopCheck.isSelected());
        itemsResult = Lists.partition(items, pageSize);
        int pageCount = itemsResult.size();
        updatePageInfo(0, pageCount);

    }

    @FXML
    public void onDetailsHeroSelect() {
        if(detailsSelectHero.getSelectionModel().getSelectedItem() != null) {
            var hero = Globals.getHeroByQualifiedName(detailsSelectHero.getSelectionModel().getSelectedItem());
            detailsSelectSlot.setItems(FXCollections.observableList(DFUtil.convert(hero.getValidSlots().values(), WearableItemSlot::getName)));
            detailsSelectSlot.getSelectionModel().selectFirst();
        }
    }

    @FXML
    public void onWearableInstall() {
        if (itemSearchResults.getChildren() != null) {
            var item = itemSearchResults.getSelected().getItem();
            var slot = Globals.getHeroByQualifiedName(detailsSelectHero.getSelectionModel().getSelectedItem()).getSlots().get(
                    detailsSelectSlot.getSelectionModel().getSelectedItem()
            );
            if (!ItemsToInstall.getInstance().isItemEnqueued(slot)) {
                var uninst = ItemsToInstall.getInstance().install(slot, item);
                if(uninst != null){
                    installQueue.deselect();
                    installQueue.getChildren().remove(installQueue.getSelected());
                }
                var tile = new WearableItemResultTile(item);
                tile.setSlot(slot);
                installQueue.addSelectables(tile);
            }
        }
    }

    @FXML
    public void onRemoveFromWearableQueue() {
        if (installQueue.getSelected() != null) {
            var item = installQueue.getSelected().getItem();
            var slot = installQueue.getSelected().getSlot();
            if (ItemsToInstall.getInstance().isItemEnqueued(slot)) {
                ItemsToInstall.getInstance().uninstall(slot);
                installQueue.getChildren().remove(installQueue.getSelected());
                installQueue.deselect();
            }
        }
    }

    @FXML
    public void onInstallWearableToGame() {
        try {
            ItemsToInstall.getInstance().performInstall(OmniPatcherV2.dotaRootDir);
        } catch (IOException e) {
            throw new IllegalStateException(new ExceptionInfo().toString(), e);
        }
    }

    @FXML
    public void onNextPage() {
        page = (int) DFUtil.clamp(0, pageCount - 1, page + 1);
        updatePageInfo(page, pageCount);
    }

    @FXML
    public void onPrevPage() {
        page = (int) DFUtil.clamp(0, pageCount - 1, page - 1);
        updatePageInfo(page, pageCount);
    }

    private void updatePageInfo(int pageNumber, int pageCount) {
        setWearableProgress(0);
        loadingProgressOverlay.setVisible(true);
        new Thread(() -> {
            this.pageCount = pageCount;
            this.page = pageNumber;
            Platform.runLater(() -> pageInfoLabel.setText(String.format(PAGE_INFO_PATTERN, pageNumber + 1, pageCount)));
            updateItemResultTiles(pageNumber);
        }).start();
    }

    private void updateItemResultTiles(int page) {
        List<WearableItemResultTile> tiles = new ArrayList<>();
        if (itemsResult.size() > 0) {
            var items = itemsResult.get(Math.min(itemsResult.size() - 1, page));
            for (int i = 0; i < items.size(); i++) {
                tiles.add(new WearableItemResultTile(items.get(i)));
                setWearableProgress(((float) i + 1) / items.size());
            }
        }
        Platform.runLater(() -> {
            itemSearchResults.deselect();
            itemSearchResults.getChildren().clear();
            itemSearchResults.addAllSelectables(tiles);
            loadingProgressOverlay.setVisible(false);
        });
    }

    @Override
    public void setup(Stage primaryStage) {
        heroSelector.setItems(FXCollections.observableList(getHeroSelectorValues(true)));
        heroSelector.getSelectionModel().select(0);

        slotSelector.setItems(FXCollections.observableList(new ArrayList<>(List.of(ANY_SELECTOR_ELEMENT))));
        slotSelector.getSelectionModel().select(0);

        var rarities = StreamEx.of(ItemRarity.values())
                .map(ItemRarity::name)
                .toList();
        raritiesSelector.getItems().clear();
        raritiesSelector.getItems().addAll(rarities);

        loadingProgressOverlay.managedProperty().bind(loadingProgressOverlay.visibleProperty());
        primaryStage.setOnCloseRequest((event) -> {
                    Platform.exit();
                    System.exit(0);
                }
        );
        itemSearchResults = new VBoxWithSelection<>(this::onSelect);
        searchResScrl.setContent(itemSearchResults);

        installQueue = new VBoxWithSelection<>(this::onSelectInQueue);
        installedScrlPane.setContent(installQueue);
        onSearch();
    }

    private void onSelectInQueue(WearableItemResultTile wearableItemResultTile) {
        //Dont need anything yet
    }

    private List<String> getHeroSelectorValues(boolean addAny) {
        var heroNames = StreamEx.of(Globals.getHEROES().values()).map(Hero::getQualifiedName).sorted().toList();
        if (addAny) {
            heroNames.add(0, ANY_SELECTOR_ELEMENT);
        }
        return heroNames;
    }

    private void onSelect(WearableItemResultTile tile) {
        var item = tile.getItem();
        detailsItemNameLabel.setText(item.getQualifiedName());

        detailsImageView.setImage(ValveResourceManager.getIcon(ICONS_PATH_PREFIX + item.getImagePath()));

        detailsHero.setText(item.getHero().getQualifiedName());

        detailsSlot.setText(item.getSlot().getName());

        detailsSelectHero.setItems(FXCollections.observableList(getHeroSelectorValues(false)));
        detailsSelectHero.getSelectionModel().select(item.getHero().getQualifiedName());

        detailsSelectSlot.setItems(FXCollections.observableList(DFUtil.convert(item.getHero().getValidSlots().values(), WearableItemSlot::getName)));
        detailsSelectSlot.getSelectionModel().select(item.getSlot().getName());
    }

    private void setWearableProgress(double value) {
        wearableSearchProgressBar.setProgress(value);
    }

    private String getValueWithAny(ComboBox<String> box) {
        return box.getSelectionModel().getSelectedIndex() > 0 ? box.getSelectionModel().getSelectedItem() : null;
    }

    private int pageSize() {
        int val = 20;
        try {
            val = Integer.parseInt(pageSizeInput.getCharacters().toString());
        } catch (NumberFormatException e) {
            //this is fine
        }
        val = Math.max(1, val);
        pageSizeInput.setText(String.valueOf(val));
        return val;
    }
}
