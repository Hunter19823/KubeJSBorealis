function tableToJson(table) {
    var data = {rows:[]};
    var sortableData = {};
    for (var i=1; i<table.rows.length; i++) {
        var tableRow = table.rows[i];
        Object.keys(tableRow.dataset).forEach( key =>{
            if(sortableData[key] === undefined){
                sortableData[key] = {key:key,possible:[]}
            }
            if(!sortableData[key].possible.includes(tableRow.dataset[key])){
                sortableData[key].possible.push(tableRow.dataset[key]);
            }
        });
        data['rows'].push(tableRow);
    }
    Object.keys(sortableData).forEach( key => {
        sortableData[key].possible.sort(function(value1,value2){
            let obfuscatedName1 = value1.includes('func') ? true : value1.includes('field');
            let obfuscatedName2 = value2.includes('func') ? true : value2.includes('field');
            switch (true) {
                case value1 > value2:
                    if(!obfuscatedName1) {
                        return -1;
                    }else{
                        if(obfuscatedName2){
                            return -1;
                        }else{
                            return 1;
                        }
                    }
                case value1 < value2:
                    return 1;
                case value1 === value2:
                    return 0;
            }
        });
    });
    data['sortKeys'] = sortableData;
    data['tbody'] = table.getElementsByTagName('tbody')[0];
    return data;
}

const sortColumn = function(index, key, value) {
    let tableJson = tableData[index];

    let rows = tableJson.rows;
    let tableBody = tableJson['tbody'];
    let newRows = Array.from(rows);

    let invert = true;
    newRows.sort(function(rowA, rowB) {

        let cellA = rowA.dataset[key];
        let cellB = rowB.dataset[key];
        if(invert){
            [cellA,cellB] = [cellB, cellA];
        }

        if(cellA === value){
            if(cellB === value){
                return 0;
            }else{
                return 1;
            }
        }else if(cellB === value){
            return -1;
        }else{
            return 0;
        }
    });


    [].forEach.call(rows, function(row) {
        tableBody.removeChild(row);
    });


    newRows.forEach(function(newRow) {
        tableBody.appendChild(newRow);
    });

    tableJson.rows = newRows;
};
const tableData = {};
let tables = document.getElementsByTagName('table');
for(let i=0; i<tables.length; i++){
    let table = tables[i];
    let tableJson = tableToJson(table);
    tableData[i] = tableJson;
    if(tableJson.sortKeys !== undefined) {
        let sortText = document.createElement('span');
        sortText.innerText="Sort By: ";

        let select = document.createElement('select');
        select.id = 'table: '+i;
        Object.keys(tableJson.sortKeys).forEach(sortKey => {
            let sort = tableJson.sortKeys[sortKey];
            let parentNode;
            parentNode = document.createElement('optgroup');
            parentNode.label = sort.key;
            select.appendChild(parentNode);
            sort.possible.forEach(value => {
                let option = document.createElement('option');
                option.value = sort.key+":"+value;
                option.text = value;
                parentNode.appendChild(option);
            });
        });
        select.addEventListener('change',function(event){
            let tableID = parseInt(this.id.substring(6));
            let sortData = event.target.value.split(':');
            sortColumn(tableID, sortData[0], sortData[1]);
        });
        sortText.appendChild(select);
        table.prepend(sortText);

        let sortData = select.value.split(':');
        sortColumn(i,sortData[0],sortData[1]);
    }
}
