let category;

function dragstart_handler(ev) {
    if (running) {
        ev.preventDefault();
        return false;
    }
    ev.dataTransfer.setData("id", ev.target.id);
    category = ev.target.parentElement.id;
    ev.dataTransfer.setData("category", category);
    ev.dataTransfer.effectAllowed = "move";
}

function dragover_handler(ev) {
    let el = ev.target;

    while (el.id !== "pre" && el.id !== "post") {
        el = el.parentElement;
    }

    if (el.id === category) {
        ev.dataTransfer.dropEffect = "move";
        ev.preventDefault();
    }
}

/*function post_dragover_handler(ev) {
    console.log(ev);
}*/

function drop_handler(ev) {
    ev.preventDefault();

    const id = ev.dataTransfer.getData("id");
    const category = ev.dataTransfer.getData("category");

    const el = document.getElementById(id);
    const parent = document.getElementById(category);

    if (ev.target.id !== "pre" && ev.target.id !== "post") {
        // Find the first parent element that has the class "card"
        let card = ev.target;
        while (card.className !== "card") {
            card = card.parentElement;
        }
        // Find out if we insert before or after it.
        // If the target (`card`) comes before the original (`el`)
        // we insert before it, otherwise after
        const index1 = getElementIndex(card);
        const index2 = getElementIndex(el);
        if (index1 < index2)
            card.insertAdjacentElement("beforebegin", el);
        else if(index1 > index2)
            card.insertAdjacentElement("afterend", el);
    } else
        parent.appendChild(el);
}

function getElementIndex(el) {
    let index = 0;
    while (el.previousElementSibling) {
        el = el.previousElementSibling;
        if (el.nodeType === 1)
            index++;
    }
    return index;
}
