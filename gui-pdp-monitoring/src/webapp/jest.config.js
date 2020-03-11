module.exports = {
    collectCoverageFrom: [
        'js/*.js'
    ],

    transform: {
        "^.+\\.[t|j]sx?$": "babel-jest"
    },

    coverageDirectory: '<rootDir>/../../target/jscoverage',
};