const preContainer = document.getElementById("pre");
const postContainer = document.getElementById("post");
const clustering = document.getElementById("clustering");

/**
 * @param {string} card
 */
function createCard(card) {
    let content, container;
    switch (card) {
        case "sort":
            content = createSortCard();
            container = preContainer;
            break;
        case "region":
            content = createRegionCard();
            container = preContainer;
            break;
        case "zones":
            content = createZonesCard();
            container = preContainer;
            break;
        case "2in1":
            content = create2in1Card();
            container = postContainer;
            break;
        case "cutting":
            content = createCuttingCard();
            container = postContainer;
            break;
        default:
            console.error(`Unknown card type "${card}"`);
            return;
    }

    const id = crypto.randomUUID();

    const div = document.createElement("div");
    div.classList.add("card");
    div.dataset.type = card;
    div.draggable = true;
    div.id = id;
    div.addEventListener("dragstart", dragstart_handler);
    div.addEventListener("drop", drop_handler);

    const data = createCardData(() => container.removeChild(div));

    div.append(...content, data);

    container.append(div);
}

function createCardData(delFunc) {
    const div = document.createElement("div");
    div.classList.add("data");

    const label = document.createElement("label");
    label.textContent = "";

    const del = document.createElement("button");
    del.textContent = "Delete";
    del.classList.add("delete");

    del.addEventListener("click", delFunc);

    div.append(label, del);

    return div;
}

/**
 * @param {string} name
 * @param {string[]} options
 * @param {string} defaultOption
 */
function createSelect(name, options, defaultOption = null) {
    const uuid = crypto.randomUUID();

    const field = document.createElement("div");
    const fieldName = document.createElement("label");
    const fieldInp = document.createElement("select");
    fieldName.htmlFor = uuid;
    fieldName.textContent = name
    fieldInp.id = uuid;

    fieldInp.append(...options.map(op => {
        const opt = document.createElement("option");
        opt.value = op.toLowerCase();
        opt.textContent = op;
        if (defaultOption !== null && opt.value === defaultOption.toLowerCase())
            opt.defaultSelected = true;
        return opt;
    }));

    field.append(fieldName, fieldInp);

    return field;
}

/**
 * @param {string} name
 * @param {number} defaultValue
 */
function createNumberInput(name, defaultValue) {
    const uuid = crypto.randomUUID();

    const field = document.createElement("div");
    const fieldName = document.createElement("label");
    const fieldInp = document.createElement("input");
    fieldName.htmlFor = uuid;
    fieldName.textContent = name
    fieldInp.id = uuid;
    fieldInp.type = "number";
    fieldInp.value = defaultValue.toString();

    field.append(fieldName, fieldInp);

    return field;
}

function createSortCard() {
    const label = document.createElement("label");
    label.textContent = "Sort";

    const field1 = createSelect("By: ", ["Density", "PIR", "CIR", "Service"], "PIR")

    const field2 = createSelect("Order: ", ["Ascending", "Descending"], "Descending");

    return [label, field1, field2];
}

function createRegionCard() {
    const label = document.createElement("label");
    label.textContent = "Region";

    const field1 = createNumberInput("Amount: ", 10);

    return [label, field1];
}

function create2in1Card() {
    const label = document.createElement("label");
    label.textContent = "2 in 1";

    return [label];
}

function createZonesCard() {
    const label = document.createElement("label");
    label.textContent = "Connected Zones";

    return [label];
}

function createCuttingCard() {
    const label = document.createElement("label");
    label.textContent = "Cluster Cutting";

    const field1 = createNumberInput("Threshold: ", 10);

    return [label, field1];
}

function setupClustering() {
    const method = document.getElementById("clustering");

    const label = document.createElement("label");
    label.textContent = "Clustering";

    const field1 = createSelect("Method:", ["1", "2", "3"]);
    const field2 = createSelect("Algorithm:", ["LatLon", "XYZ", "Circle"], "xyz");

    method.append(label, field1, field2);
}

createCard("sort");
createCard("region");
// createCard("zones");
createCard("2in1");
// createCard("cutting");
setupClustering();

preContainer.addEventListener("dragover", dragover_handler);
postContainer.addEventListener("dragover", dragover_handler);
