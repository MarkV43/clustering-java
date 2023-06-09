const url = "http://localhost:8080/";
let began = false;
let listener;
let running;
let stopping;

function status(response) {
    if (response.status >= 200 && response.status < 300) {
        return Promise.resolve(response)
    } else {
        return Promise.reject(new Error(response.statusText))
    }
}

function getPre() {
    const pre = [];
    for (let child of preContainer.children) {
        const obj = {
            name: child.dataset.type
        };
        switch (obj.name) {
            case "sort":
                obj.by = child.children[1].children[1].value;
                obj.order = child.children[2].children[1].value;
                break;
            case "region":
                obj.amount = child.children[1].children[1].value;
                break;
            case "zones":
                break;
            default:
                console.error(`Unknown card type "${obj.name}"`);
                return;
        }

        pre.push(obj);
    }

    return pre;
}

function getPost() {
    const post = [];
    for (let child of postContainer.children) {
        const obj = {
            name: child.dataset.type
        };
        switch (obj.name) {
            case "2in1":
                break;
            case "cutting":
                obj.threshold = child.children[1].children[1].value;
                break;
            default:
                console.error(`Unknown card type "${obj.name}"`);
                return;
        }

        post.push(obj);
    }

    return post;
}

function getClustering() {
    return {
        method: clustering.children[1].children[1].value,
        metric: clustering.children[2].children[1].value,
        algorithm: clustering.children[3].children[1].value,
    };
}

function stop() {
    fetch(url + 'stop')
        .then(status)
        .then(r => r.json())
        .then(r => {
            console.log(r);
            if (r.status.toLowerCase() === "error") {
                alert(r.message);
            } else {
                stopping = true;
            }
        })
        .catch(e => console.error("Request failed.", e));
}

function run() {
    const pre = getPre();
    const post = getPost();
    const clustering = getClustering();

    console.log({body: {pre, clustering, post}});

    fetch(url + 'run', {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({pre, clustering, post}),
    }).then(status)
        .then(r => r.json())
        .then(r => {
            console.log(r);
            if (r.status.toLowerCase() === "error") {
                alert(r.message);
            } else {
                began = false;
                listener = setInterval(listen, 100);
            }
        })
        .catch(e => console.error("Request failed.", e));
}


/**
 * @param {number} num
 * @return {string}
 */
function formatNumber(num) {
    const str = num + '';
    const [left, right] = str.split('.');
    if (left.length > 4) {
        return Math.round(num) + '';
    }

    if (left.length > 1 || left[0] !== '0') {
        return (Math.round(num * 100) / 100) + '';
    }

    const decPlaces = -Math.floor(Math.log10(num)) + 1;
    const exp = Math.pow(10, decPlaces + 1);
    return (Math.round(num * exp) / exp) + '';
}

/**
 * @param {string} unit
 * @return {string}
 */
function formatUnit(unit) {
    switch (unit.toUpperCase()) {
        case "PERCENTAGE":
            return "%";
        case "UES":
            return " UEs";
        case "KBPS":
            return " kbps";
        case "NOUNIT":
            return "";
        default:
            throw new Error(`Unknown unit ${unit}`);
    }
}

function _updateCardData(card, data) {
    const time = card.children[card.children.length-1].children[0];
    const amount = card.children[card.children.length-1].children[1];
    const eval = card.children[card.children.length-1].children[2];

    if (data.durationMs >= 0) {
        time.textContent = `${prettyMs(data.durationMs)}`;
        if (data.clusterAmount > 0) {
            amount.textContent = `${data.clusterAmount}`;

            console.log(data.eval);

            evalHtml = "<table><tbody>";
            // Go through all evals and add them to `eval`
            for (let [key, value] of Object.entries(data.eval)) {
                evalHtml += `<tr><td>${key}</td><td>${formatNumber(value.value)}${formatUnit(value.unit)}</td></tr>`;
            }
            evalHtml += "</tbody></table>";
            eval.innerHTML = evalHtml;
        }
    } else {
        time.textContent = "";
        amount.textContent = "";
        eval.textContent = "";
    }
}

function listen() {
    fetch(url + 'status')
        .then(status)
        .then(r => r.json())
        .then(r => {
            console.log(r);
            running = r.running;
            stopping = r.stopping;

            if (began && !running) {
                clearInterval(listener);
            } else if (!began && running)
                began = true;

            let finished = true;
            for (let i = 0; i < r.pre.length; i++){
                const card = preContainer.children[i];
                const data = r.pre[i];

                _updateCardData(card, data);
                if (data.durationMs === -1)
                    finished = false;
            }

            if (r.clustering !== null) {
                _updateCardData(clustering, r.clustering);
                if (r.clustering.durationMs === -1)
                    finished = false;
            }

            for (let i = 0; i < r.post.length; i++){
                const card = postContainer.children[i];
                const data = r.post[i];

                _updateCardData(card, data);
                if (data.durationMs === -1)
                    finished = false;
            }

            if (r.clustering !== null && finished)
                began = true;
        })
}

function setRunning(value) {
    running = value;
    document.getElementById("run").disabled = !!running;
}

function setStopping(value) {
    stopping = value;
    document.getElementById("stop").disabled = !!stopping;
}