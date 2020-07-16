let publisher = require('@pact-foundation/pact-node');
let path = require('path');

let opts = {
    pactFilesOrDirs: [path.resolve(process.cwd(), 'pacts')],
    pactBroker: 'https://tossbank.pactflow.io',
    pactBrokerToken: '<pact-broker-token>',
    consumerVersion: '2.0.0',
    tags: ['live']
};

publisher.publishPacts(opts).then(
  () => console.log("Pacts successfully published"));
