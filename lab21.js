db.pharmacy.drop();
db.drug.drop();

db.pharmacy.insertMany([
    {
        _id: 1,
        name: "CVS",
        address: "123 Main St, Anytown, USA",
        phone: "555-1234",
        drugCosts: [
            {drugName: "Aspirin", cost: 5.99},
            {drugName: "Tylenol", cost: 6.49},
            {drugName: "Ibuprofen", cost: 7.29}
        ]
    },
    {
        _id: 2,
        name: "Walgreens",
        address: "456 Oak Ave, Springfield, USA",
        phone: "555-5678",
        drugCosts: [
            {drugName: "Aspirin", cost: 6.49},
            {drugName: "Tylenol", cost: 6.99},
            {drugName: "Advil", cost: 8.49},
            {drugName: "Claritin", cost: 12.99}
        ]
    },
    {
        _id: 3,
        name: "Rite Aid",
        address: "789 Pine Rd, Centerville, USA",
        phone: "555-2468",
        drugCosts: [
            {drugName: "Aspirin", cost: 5.75},
            {drugName: "Ibuprofen", cost: 7.10},
            {drugName: "Zyrtec", cost: 14.29},
            {drugName: "Benadryl", cost: 9.59}
        ]
    },
    {
        _id: 4,
        name: "Costco Pharmacy",
        address: "200 Market Blvd, Metro City, USA",
        phone: "555-7890",
        drugCosts: [
            {drugName: "Aspirin", cost: 4.99},
            {drugName: "Tylenol", cost: 5.99},
            {drugName: "Advil", cost: 7.99},
            {drugName: "Zyrtec", cost: 13.49}
        ]
    },
    {
        _id: 5,
        name: "Walmart Pharmacy",
        address: "55 Commerce Way, Riverdale, USA",
        phone: "555-3344",
        drugCosts: [
            {drugName: "Aspirin", cost: 5.25},
            {drugName: "Tylenol", cost: 6.10},
            {drugName: "Ibuprofen", cost: 6.99},
            {drugName: "Claritin", cost: 11.99}
        ]
    }
]);

db.drug.insertMany([
    { _id: 1, name: "Aspirin" },
    { _id: 2, name: "Tylenol" },
    { _id: 3, name: "Ibuprofen" },
    { _id: 4, name: "Advil" },
    { _id: 5, name: "Claritin" },
    { _id: 6, name: "Zyrtec" },
    { _id: 7, name: "Benadryl" }
]);